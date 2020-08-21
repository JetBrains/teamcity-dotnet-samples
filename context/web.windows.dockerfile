ARG baseImage
FROM ${baseImage}
RUN mkdir app
COPY Clock.Web/win-x64/* /app/
WORKDIR /app
ENV ASPNETCORE_URLS=http://+:5000
EXPOSE 5000
ENTRYPOINT ["/app/Clock.Web.exe"]