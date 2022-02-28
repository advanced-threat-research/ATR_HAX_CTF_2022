FROM ubuntu:20.04
WORKDIR /
ENV DEBIAN_FRONTEND=noninteractive
RUN dpkg --add-architecture i386 && apt update && apt install -y libc6:i386 libstdc++6:i386
COPY ./proto /
COPY ./access.txt /
EXPOSE 5555
ENTRYPOINT ["/proto"]
