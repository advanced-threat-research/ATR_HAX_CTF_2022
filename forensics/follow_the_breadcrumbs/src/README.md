While the challenge folder contains the publicly viewable content of the challenge, the src folder should hold any code necessary to generate the challenge. This includes scripts, source code, or anything else human readable that needs to be run. Key files, certificates, or other digital assets should live in the assets folder.

To create a new challenge, find an image in some lossless format (jpg2000, png, tiff, bmp -- recommend it be relatively small, tens of kb at most), and run the stego.py script (see script for options) on the image. This will create a "new_[image].png" file.

The breadcrumbs.py file can be used both to create and solve this challenge. Comment as appropriate.