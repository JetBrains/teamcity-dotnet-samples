ARG baseImage
FROM ${baseImage}
RUN mkdir app
COPY Clock.Console/win-x64 /app/
WORKDIR /app
ENTRYPOINT ["/app/Clock.Console.exe"]
