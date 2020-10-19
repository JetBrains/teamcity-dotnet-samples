ARG baseImage
FROM ${baseImage}
RUN mkdir app
COPY Clock.Console/linux-x64 /app/
RUN chmod +x /app/Clock.Console
WORKDIR /app
ENTRYPOINT ["/app/Clock.Console"]