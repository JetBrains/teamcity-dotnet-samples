#See https://aka.ms/containerfastmode to understand how Visual Studio uses this Dockerfile to build your images for faster debugging.

FROM mcr.microsoft.com/dotnet/core/aspnet:3.1-buster-slim AS base
WORKDIR /app
EXPOSE 5000

FROM mcr.microsoft.com/dotnet/core/sdk:3.1-buster AS build
WORKDIR /src
COPY ["Clock.Web/Clock.Web.csproj", "Clock.Web/"]
COPY ["Clock/Clock.csproj", "Clock/"]
COPY ["Clock.IoC/Clock.IoC.csproj", "Clock.IoC/"]
RUN dotnet restore "Clock.Web/Clock.Web.csproj"
COPY . .
WORKDIR "/src/Clock.Web"
RUN dotnet build "Clock.Web.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "Clock.Web.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "Clock.Web.dll"]