ARG baseImage
FROM ${baseImage}
RUN mkdir app
COPY Clock.Web/linux-x64/* /app/
RUN chmod +x /app/Clock.Web
WORKDIR /app
ENV ASPNETCORE_URLS=http://+:5000
EXPOSE 5000
ENTRYPOINT ["/app/Clock.Web"]