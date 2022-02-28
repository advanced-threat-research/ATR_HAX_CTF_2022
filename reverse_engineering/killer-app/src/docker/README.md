## Setup
1. The machine running the Docker container must be running Linux.
2. Ensure [Docker](https://docs.docker.com/engine/install/ubuntu/) and [KVM](https://help.ubuntu.com/community/KVM/Installation) are installed (`/dev/kvm` must be available).
3. `git clone ssh://git@gitlab.atrhil:2222/atr_ctf/atr_ctf_2022/killer-app.git`
4. `cd killer-app`
5. `docker build -t killer-app .`
6. Run the docker container in one of two ways:
   * For internal testing:
        `docker run --privileged -it --rm -p 5554:5554 -p 5555:5555 -p 80:8080 --name killer-app killer-app`
   * For production:
        `docker run --privileged --rm -p 80:8080 --name killer-app killer-app`

## Testing
* Once the container is running, the web interface can be accessed via port 8080 on the machine running the container.
* You can connect to the Android emulator running inside the docker container using [ADB](https://developer.android.com/studio/command-line/adb) via port 5555.
* You can connect to the [Android emulator console](https://developer.android.com/studio/run/emulator-console) via port 5554.
