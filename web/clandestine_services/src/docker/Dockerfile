FROM python:2.7.16

#Install packages
RUN apt update \
  && apt install -y sudo

#Make docker user sudoer
RUN useradd -m docker && echo "docker:docker" | chpasswd \
  && adduser docker sudo
######         
## The docker may not work so it's currently hosted at my place. 
## If you can't get to h6lblddawb5p7dxqijifj5gmazzml3rkgpfno2pnyfnw6ymg32pbiuad.onion
## Ask me to turn in on.
## I'm still troubleshooting why tor isn't working in docker.
######

#Install tor and web server
RUN sudo apt -y install tor
RUN sudo apt -y install nginx

### May not be needed. Might behave differently on ubuntu
RUN sudo touch /var/log/tor/notices.log
RUN chown debian-tor:debian-tor /var/log/tor/notices.log
####

#CREATE tor web page
RUN sudo mkdir /var/www/hidden_service/
COPY index.html /var/www/hidden_service/index.html

#Fix permissions
RUN sudo chown -R www-data:www-data /var/www/hidden_service/
RUN sudo chmod -R 755 /var/www/

#configure nginx
COPY default /etc/nginx/site-available/default
COPY hidden_service /etc/nginx/sites-available/hidden_service
COPY nginx.conf /etc/nginx/nginx.conf

#migrate over tor hidden services
COPY cheesecake /var/lib/tor/cheesecake
COPY torrc /etc/tor/torrc
RUN sudo chown -R debian-tor:debian-tor /var/lib/tor/cheesecake
RUN sudo chmod -R 700 /var/lib/tor/cheesecake
RUN sudo chown debian-tor:debian-tor /etc/tor/torrc

RUN sudo ln -sf /etc/nginx/sites-available/hidden_service /etc/nginx/sites-enabled/hidden_service

CMD sudo service tor start
RUN nginx -c /etc/nginx/nginx.conf -t
CMD sudo service nginx start

COPY server.py .

ENTRYPOINT ["python", "server.py"]
