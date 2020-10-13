#See https://aka.ms/containerfastmode to understand how Visual Studio uses this Dockerfile to build your images for faster debugging.

FROM mcr.microsoft.com/dotnet/runtime:5.0-buster-slim AS base
WORKDIR /app

FROM mcr.microsoft.com/dotnet/sdk:5.0-buster-slim AS build
WORKDIR /src
COPY ["Clock.Console/Clock.Console.csproj", "Clock.Console/"]
COPY ["Clock/Clock.csproj", "Clock/"]
COPY ["Clock.IoC/Clock.IoC.csproj", "Clock.IoC/"]
RUN dotnet restore "Clock.Console/Clock.Console.csproj"
COPY . .
WORKDIR "/src/Clock.Console"
RUN dotnet build "Clock.Console.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "Clock.Console.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "Clock.Console.dll"]