FROM httpd:alpine

RUN apk add mapserver gdal-driver-PNG gdal-driver-HEIF gdal-driver-JPEGXL gdal-driver-WMS gdal-driver-WEBP --no-cache --repository http://dl-cdn.alpinelinux.org/alpine/edge/testing

RUN sed -i -e 's/#LoadModule\ cgid_module/LoadModule\ cgid_module/g' \
           -e 's/#LoadModule\ cgi_module/LoadModule\ cgi_module/g' /usr/local/apache2/conf/httpd.conf && \
    perl -i -p0e 's/(Directory .+cgi-bin">.*?\s+Options )None/\1FollowSymLinks/s' /usr/local/apache2/conf/httpd.conf

RUN ln -s /usr/bin/mapserv /usr/local/apache2/cgi-bin/mapserv

EXPOSE 80
CMD ["httpd-foreground"]
