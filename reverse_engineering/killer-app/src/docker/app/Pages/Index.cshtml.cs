using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using System;
using System.Diagnostics;
using System.IO;
using System.IO.Compression;
using System.Security.Cryptography;
using System.Threading;
using System.Threading.Tasks;

namespace F32Ctf.Pages {
    public class IndexModel : PageModel {

        private static SemaphoreSlim _maxInstancesSemaphore = new SemaphoreSlim(1, 1);

        private readonly ILogger<IndexModel> _logger;
        private readonly IConfiguration _config;

        public IndexModel(
            ILogger<IndexModel> logger,
            IConfiguration configuration
        ) {
            _logger = logger;
            _config = configuration;
        }

        public void OnGet() { }

        [BindProperty]
        public IFormFile ApkFile { get; set; }
        public string ApkLocation => _config["ApkLocation"];
        public string ApkChecksum => _config["ApkChecksum"];
        public string Error { get; set; }
        public string Flag => _config["Flag"];
        public string ScreencapLocation { get; set; }
        public bool ShowFlag { get; set; } = false;
        public bool ShowScreencap { get; set; } = false;

        private enum ApkTestResult {
            Uninitialized      = -1,
            Success            =  0,
            FailedInstall      =  1,
            FailedStart        =  2,
            FailedUiDumpHome   =  3,
            FailedPullHome     =  4,
            FailedRemoveDialog =  5,
            SystemUIError      =  6,
            FailedCompareHome  =  7,
            FailedHelpPress    =  8,
            FailedUiDumpHelp   =  9,
            FailedPullHelp     =  10,
            FailedCompareHelp  =  11
        }

        public async Task OnPostAsync() {
            if (ApkFile == null) {
                Error = $"Nothing more embarrassing than forgetting the attachment when sending an email, am I right?";
                return;
            }

            var fileExt = Path.GetExtension(ApkFile.FileName).Substring(1).ToLower();
            if (fileExt != "apk") {
                Error = $"Not sure I can install this on my phone - double check the file extension.";
                return;
            }

            var maxFileSize = Convert.ToInt32(_config["MaxApkFileSize"]);
            if (ApkFile.Length > maxFileSize) {
                Error = $"Looks like the app itself has been hitting the gym, cause this thing is HUGE! Send me one that fits within the 2MB attachment file size limit.";
                return;
            }

            var apkPath = $"/tmp/{Guid.NewGuid().ToString("N")}.apk";
            ScreencapLocation = $"images/{Guid.NewGuid().ToString("N")}.png";
            try {
                using (var reader = ApkFile.OpenReadStream()) {
                    reader.Position = 0;
                    using (var sha256 = SHA256.Create()) {
                        var hash = sha256.ComputeHash(reader);
                        var checksum = BitConverter.ToString(hash).Replace("-", "").ToLowerInvariant();
                        if (checksum == ApkChecksum) {
                            Error = $"This is like déjà vu all over again. Are you sure you modified this thing?";
                            return;
                        }
                    }

                    reader.Position = 0;
                    using (var writer = System.IO.File.OpenWrite(apkPath)) {
                        await reader.CopyToAsync(writer);
                    }
                }

                if (!IsZipValid(apkPath)) {
                    Error = $"Hmm, the file I received doesn't appear to be an APK. Could this be one of those \"malware\" things I keep hearing about?";
                    return;
                }

                await _maxInstancesSemaphore.WaitAsync();
                try {
                    var startInfo = new ProcessStartInfo();
                    startInfo.WindowStyle = ProcessWindowStyle.Hidden;
                    startInfo.FileName = "bash";
                    startInfo.Arguments = $"run-test.sh \"{apkPath}\" \"{ScreencapLocation}\"";
                    startInfo.RedirectStandardInput = false;
                    startInfo.RedirectStandardOutput = false;
                    startInfo.RedirectStandardError = false;
                    startInfo.CreateNoWindow = true;

                    var process = new Process();
                    process.StartInfo = startInfo;
                    process.Start();
                    if (!process.WaitForExit(Convert.ToInt32(_config["ScriptTimeout"]))) {
                        try {
                            process.Kill(true);
                        } catch (InvalidOperationException) { }
                        Error = $"I encountered a timeout while trying to install/run the app. :(";
                        return;
                    }

                    var apkTestResult = ApkTestResult.Uninitialized;
                    if (process.ExitCode >= 100) {
                        ShowScreencap = true;
                        apkTestResult = (ApkTestResult)(process.ExitCode - 100);
                    } else {
                        apkTestResult = (ApkTestResult)process.ExitCode;
                    }

                    switch (apkTestResult) {
                        case ApkTestResult.Success:
                            ShowFlag = true;
                            break;
                        case ApkTestResult.FailedInstall:
                            Error = $"I tried installing it and no dice. You broke it, huh?";
                            return;
                        case ApkTestResult.FailedStart:
                            Error = $"I managed to install the thing, but it just won't run. You must've really went to town on it.";
                            return;
                        case ApkTestResult.FailedRemoveDialog:
                            Error = $"I'm still seeing the popup dialog. :(";
                            return;
                        case ApkTestResult.SystemUIError:
                            Error = $"Looks like the emulator encountered a System UI error and couldn't start the app. Oops.";
                            return;
                        case ApkTestResult.FailedCompareHome:
                        case ApkTestResult.FailedCompareHelp:
                            Error = $"Well it runs, but it doesn't look much like the one I sent you. Maybe cut back on the creative liberty?";
                            return;
                        default:
                            Error = $"Something went horribly wrong. This one might be my bad.";
                            return;
                    }
                }
                finally {
                    _maxInstancesSemaphore.Release();
                }

            } finally {
                if (System.IO.File.Exists(apkPath)) {
                    System.IO.File.Delete(apkPath);
                }
            }
        }

        private static bool IsZipValid(string path) {
            try {
                using (var zipFile = ZipFile.OpenRead(path)) {
                    var entries = zipFile.Entries;
                    return true;
                }
            } catch (InvalidDataException) {
                return false;
            }
        }
    }
}
