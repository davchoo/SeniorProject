FROM httpd:alpine

ENV FCGID_VERSION 2.3.9
ENV FCGID_SHA256 1cbad345e3376b5d7c8f9a62b471edd7fa892695b90b79502f326b4692a679cf
ENV FCGID_SRC "mod_fcgid-${FCGID_VERSION}.tar.gz"
RUN mkdir ./fcgid && \
    cd ./fcgid && \
    wget -O "$FCGID_SRC" "https://dlcdn.apache.org/httpd/mod_fcgid/$FCGID_SRC" && \
    # We should also verify using signatures, but :shrug:
    echo "$FCGID_SHA256 *$FCGID_SRC" | sha256sum -c - && \
    tar --strip-components 1 -xzf $FCGID_SRC && \
	apk add --no-cache --virtual .fcgid-build-deps \
		apr-dev \
		apr-util-dev \
		coreutils \
		dpkg-dev dpkg \
		gcc \
		libc-dev \
        make
RUN cd ./fcgid && \
    ./configure.apxs && \
    make -j $(nproc) && \
    make install && \
    cd .. && \
    rm -r ./fcgid && \
    apk del --no-cache .fcgid-build-deps

RUN apk add mapserver gdal-driver-PNG gdal-driver-WMS --no-cache --repository https://dl-cdn.alpinelinux.org/alpine/edge/testing

RUN sed -i -e 's/#LoadModule\ cgid_module/LoadModule\ cgid_module/g' \
           -e 's/#LoadModule\ cgi_module/LoadModule\ cgi_module/g' $HTTPD_PREFIX/conf/httpd.conf && \
    perl -i -p0e 's/(Directory .+cgi-bin">.*?\s+Options )None/\1FollowSymLinks/s' $HTTPD_PREFIX/conf/httpd.conf && \
    printf "<IfModule fcgid_module>\nAddHandler fcgid-script .fcgi\nFcgidMaxProcessesPerClass 5\n</IfModule>\n" >> $HTTPD_PREFIX/conf/httpd.conf

RUN ln -s /usr/bin/mapserv /usr/local/apache2/cgi-bin/mapserv.fcgi

EXPOSE 80
CMD ["httpd-foreground"]
