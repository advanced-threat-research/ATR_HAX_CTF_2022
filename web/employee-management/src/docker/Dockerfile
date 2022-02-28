FROM mysql:5.7

# Install packages
ENV DEBIAN_FRONTEND noninteractive
RUN apt update -q && apt install -y php-mysql mysql-server apache2 php libapache2-mod-php vim cron

COPY ./php/sql_ctf_20200819.sql /root/backup.sql
COPY ./php/.my.cnf /root/.my.cnf
COPY ./apache/demo.apache.conf /usr/local/apache2/conf/demo.apache.conf
COPY ./public_html /var/www/html/
COPY ./dbconfig /var/www/

ENV MYSQL_ROOT_PASSWORD "rootpassword"

RUN rm /var/www/html/index.html
RUN echo "Include /usr/local/apache2/conf/demo.apache.conf" >> /usr/local/apache2/conf/httpd.conf

# clean db every 15 Minutes
COPY cleandb-cron /etc/cron.d/cleandb-cron
RUN chmod 0744 /etc/cron.d/cleandb-cron
RUN crontab /etc/cron.d/cleandb-cron
RUN touch /var/log/cron.log

COPY ./cleandb.sh /root/cleandb.sh
COPY ./start.sh /start.sh
#COPY ./docker-entrypoint.sh /entrypoint.sh
# Start apache
#CMD ["/usr/sbin/apache2ctl", "-D", "FOREGROUND", "-f", "/usr/local/apache2/conf/demo.apache.conf"]

EXPOSE 80

ENTRYPOINT ["/entrypoint.sh"]
CMD ["/start.sh"]
