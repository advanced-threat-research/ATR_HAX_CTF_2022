FROM php:apache
#RUN mkdir -p /var/www/keygen/html
COPY 000-default.conf /etc/apache2/sites-available/000-default.conf
COPY ./keygen /var/www/keygen
COPY ./html /var/www/html
RUN touch /var/log/skeletonkey.log
RUN chown -R www-data:www-data /var/www/keygen
RUN chown www-data:www-data /var/log/skeletonkey.log
EXPOSE 80
