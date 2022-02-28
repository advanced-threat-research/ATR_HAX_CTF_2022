ARG PHP_VERSION=""
FROM php:${PHP_VERSION:+${PHP_VERSION}-}fpm-alpine

RUN apk update; \
    apk upgrade;

RUN docker-php-ext-install mysqli
RUN docker-php-ext-install pdo_mysql

COPY .my.cnf /root/.my.cnf
COPY sql_ctf_20200819.sql /root/sql_ctf_20200819.sql 
