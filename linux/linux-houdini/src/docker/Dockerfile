FROM ubuntu:20.04
EXPOSE 1337

ENV DEBIAN_FRONTEND=noninteractive
RUN apt update && apt install -y binutils build-essential vim cron socat openssl

RUN mkdir /private
COPY flag /private/flag
RUN chown -R root:root /private && chmod -R 400 /private
COPY echo_flag /usr/bin/echo_flag
RUN chmod +s /usr/bin/echo_flag
COPY entrypoint.sh entrypoint.sh
RUN chmod 700 entrypoint.sh
COPY ./drop.sh /root/drop.sh
RUN chmod 700 /root/drop.sh
COPY ./.bashrc /etc/skel/.bashrc
COPY ./help_with_shell.txt /etc/skel/help_with_shell.txt

# Reboot every night
COPY reboot-cron /etc/cron.d/reboot-cron
RUN chmod 0744 /etc/cron.d/reboot-cron
RUN crontab /etc/cron.d/reboot-cron
RUN touch /var/log/cron.log

CMD [ "./entrypoint.sh" ]
