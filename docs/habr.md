Мы в TeamCity всегда уделяли особое внимание .NET, его многочисленным инструментам и фреймворкам тестирования. В этом посте мы хотим рассказать о недавних обновлениях в нашей поддержке .NET и поделиться примером демо-проекта, который их иллюстрирует.

<cut/>

Сейчас поддержка .NET в TeamCity реализована с помощью огромного набора специализированных «[ранеров](https://www.jetbrains.com/help/teamcity/build-runner.html)» и «[билд фичей](https://www.jetbrains.com/help/teamcity/adding-build-features.html)». Ранеры обеспечивают интеграцию билда со сторонним софтом, а фичи выступают функциональными надстройками билда.

До версии 2020.1, TeamCity предоставлял следующие .NET компоненты:

* [MSBuild](https://www.jetbrains.com/help/teamcity/msbuild.html)﻿﻿ – ранер с поддержкой MSBuild и [Mono](https://www.mono-project.com/docs/) XBuild
* [Visual Studio (sln)](https://www.jetbrains.com/help/teamcity/visual-studio-sln.html) – ранер, имитирующий [Visual Studio IDE (devenv)](https://docs.microsoft.com/en-us/visualstudio/ide/reference/devenv-command-line-switches), используя MSBuild
* [Visual Studio 2003](https://www.jetbrains.com/help/teamcity/visual-studio-2003.html) – то же что и предыдущий ранер, но с учетом специфики Visual Studio 2003
* [Visual Studio Tests](https://www.jetbrains.com/help/teamcity/visual-studio-tests.html) – ранер, запускающий Visual Studio и MS тесты
* [.NET Process Runner](https://www.jetbrains.com/help/teamcity/net-process-runner.html) – ранер, запускающий .NET приложения
* [.NET CLI Runner](https://blog.jetbrains.com/teamcity/2016/11/teamcity-dotnet-core/) – ранер, обеспечивающий интеграцию TeamCity и [.NET Core CLI](https://docs.microsoft.com/en-us/dotnet/core/tools/)
* [NUnit](https://www.jetbrains.com/help/teamcity/nunit.html) – ранер, запускающий NUnit-тесты
* Набор ранеров для NuGet – поддержка утилиты [nuget.exe CLI для Windows](https://docs.microsoft.com/en-us/nuget/consume-packages/install-use-packages-nuget-cli)
  * [NuGet Pack](https://www.jetbrains.com/help/teamcity/nuget-pack.html)
  * [NuGet Publish](https://www.jetbrains.com/help/teamcity/nuget-publish.html)
  * [NuGet Installer](https://www.jetbrains.com/help/teamcity/nuget-installer.html)
* Встроенный в TeamCity [репозиторий пакетов NuGet](https://www.jetbrains.com/help/teamcity/using-teamcity-as-nuget-feed.html#Enabling+NuGet+Feed)
* [TeamCity symbol server](https://github.com/JetBrains/teamcity-symbol-server) – накапливает и предоставляет отладочную информацию
* Интеграция с [Azure DevOps](https://azure.microsoft.com/en-us/services/devops/) (ранее Team Foundation Server)
* [Duplicates Finder (ReSharper)](https://www.jetbrains.com/help/teamcity/duplicates-finder-resharper.html) – поиск дублирования кода
* [Inspections (ReSharper)](https://www.jetbrains.com/help/teamcity/inspections-resharper.html) – инспекции кода на базе [JetBrains Resharper](https://www.jetbrains.com/help/resharper/Code_Analysis__Code_Inspections.html)
* [FxCop](https://www.jetbrains.com/help/teamcity/fxcop.html) – статический анализ качества кода на базе FxCop
* [JetBrains dotTrace](https://github.com/JetBrains/teamcity-dottrace) – тестирование производительности приложений с [dotTrace Command-Line Profiler](https://www.jetbrains.com/help/profiler/Performance_Profiling__Profiling_Using_the_Command_Line.html)
* [JetBrains dotMemory Unit](https://github.com/JetBrains/teamcity-dotmemory) – тестирование проблем памяти с [JetBrains dotMemory Unit](https://www.jetbrains.com/dotmemory/unit/)
* [Поддержка](https://github.com/JetBrains/teamcity-unity-plugin) платформы разработки 3D-приложений Unity

Такое разнообразие компонентов позволяет TeamCity использовать весь потенциал .NET, но имеет и минусы. Например, чтобы правильно сгруппировать части проекта и выбрать ранеры, чтобы построить приложения и протестировать их на требуемых ОС, каждый раз приходится учитывать множество факторов:

* Тип проекта
* Целевая платформа
* .NET-фреймворк
* Тестовый фреймворк
* Операционная система для тестирования

и т.д.

К счастью, с появлением .NET Core с открытым кодом и поддержкой кроссплатформенности, Microsoft постарались унифицировать и упорядочить инструменты разработки, объединив их в [.NET SDK](https://github.com/dotnet/sdk). Следующим шагом развития стал [.NET 5](https://dotnet.microsoft.com/download/dotnet/5.0) – он объединил .NET Core, .NET Framework, Xamarin и Mono. С версии 2020.1 TeamCity также объединил большинство своих компонентов, отвечающих за построение, тестирование и развертывание проектов, в один ранер [.NET](https://www.jetbrains.com/help/teamcity/net.html). Он консолидирует возможности ранеров из списка выше и предоставляет унифицированный подход. Мы надеемся, что это сильно упростит работу с .NET для наших пользователей. Новый ранер поддерживает:

*   Команды [.NET CLI](https://docs.microsoft.com/en-us/dotnet/core/tools/)
*   Windows и кроссплатформенный MSBuild
*   «Честный» Visual Studio IDE (devenv)
*   Запуск Windows и кроссплатформенных тестов, в том числе NUnit и XUnit
*   Запуск Windows, .NET процессов и командных скриптов на разных операционных системах
*   Кроссплатформенную статистику покрытия кода
*   [Docker](https://www.jetbrains.com/help/teamcity/docker-wrapper.html)

Мы будем ограниченно поддерживать устаревшие ранеры, чтобы обеспечить плавную миграцию проектов на единый ранер .NET, но дальнейшего развития они не получат. Рекомендуем учесть это при создании новых конфигураций и при переходе на .NET 5 из последних версии Visual Studio и [Rider](https://www.jetbrains.com/rider/).

## Структура демо-проекта

Технически, ранер .NET – результат глубокой переработки привычного пользователям раннера [.NET CLI](https://plugins.jetbrains.com/plugin/9190--net-cli-support). В нём появилась масса новых возможностей, которые мы хотим показать на примере демонстрационного .NET-проекта. Его исходный код и скрипт конфигураций TeamCity находятся [в этом репозитории](https://github.com/JetBrains/teamcity-dotnet-samples). А уже развернутый проект TeamCity можно посмотреть [на этом демо-сервере](https://teamcity.jetbrains.com/project.html?projectId=DemoProjects_TeamCity_Net).

Демо-проект .NET состоит из нескольких .NET проектов:

<table>
<tbody>
<tr class="odd">
<td align="left"><p>Название</p></td>
<td align="left"><p>Тип проекта</p></td>
<td align="left"><p>Описание</p></td>
<td align="left"><p>Конфигурации TeamCity</p></td>
<td align="left"><p>Развертывание</p></td>
</tr>
<tr class="even">
<td align="left"><p><a href="https://github.com/JetBrains/teamcity-dotnet-samples/tree/master/Clock">Clock</a></p></td>
<td align="left"><p>.NET Standard 1.2</p></td>
<td align="left"><p>Библиотека общей логики</p></td>
<td align="left"><p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_Pack">Создание пакета</a></p>
<p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_PublishToNuget">NuGet публикация</a></p></td>
<td align="left"><p>NuGet</p></td>
</tr>
<tr class="odd">
<td align="left"><p><a href="https://github.com/JetBrains/teamcity-dotnet-samples/tree/master/Clock.IoC">Clock.IoC</a></p></td>
<td align="left"><p>.NET Standard 1.2</p></td>
<td align="left"><p>Библиотека настройки IoC</p></td>
<td align="left"><p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_Pack">Создание пакета</a></p>
<p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_PublishToNuget">NuGet публикация</a></p></td>
<td align="left"><p>NuGet</p></td>
</tr>
<tr class="even">
<td align="left"><p><a href="https://github.com/JetBrains/teamcity-dotnet-samples/tree/master/Clock.Tests">Clock.Tests</a></p></td>
<td align="left"><p>.NET 5.0</p></td>
<td align="left"><p>Тесты общей логики</p></td>
<td align="left"><p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_LinuxTests">Тесты Windows</a></p>
<p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_LinuxTests">Тесты Linux</a></p></td>
<td align="left"><p> </p></td>
</tr>
<tr class="odd">
<td align="left"><p><a href="https://github.com/JetBrains/teamcity-dotnet-samples/tree/master/Clock.Console">Clock.Console</a></p></td>
<td align="left"><p>.NET 5.0</p></td>
<td align="left"><p>Консольное приложение</p></td>
<td align="left"><p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_BuildConsoleAndWebWindows64">Windows x64</a></p>
<p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_BuildConsoleAndWebLinux64">Linux x64</a></p>
<p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_PushConsoleWindows2004">Docker Nanoserver</a></p>
<p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_PushConsoleUbuntu2004">Docker Ubuntu</a></p>
<p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_PushConsoleMultiArch">Docker multi-arch</a></p></td>
<td align="left"><p>Docker</p></td>
</tr>
<tr class="even">
<td align="left"><p><a href="https://github.com/JetBrains/teamcity-dotnet-samples/tree/master/Clock.Web">Clock.Web</a></p></td>
<td align="left"><p>.NET 5.0</p></td>
<td align="left"><p>Web приложение</p></td>
<td align="left"><p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_BuildConsoleAndWebWindows64">Windows x64</a></p>
<p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_BuildConsoleAndWebLinux64">Linux x64</a></p>
<p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_PushWebWindows2004">Docker Nanoserver</a></p>
<p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_PushWebUbuntu2004">Docker Ubuntu</a></p>
<p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_PushWebMultiArch">Docker multi-arch</a></p></td>
<td align="left"><p>Docker</p></td>
</tr>
<tr class="odd">
<td align="left"><p><a href="https://github.com/JetBrains/teamcity-dotnet-samples/tree/master/Clock.Desktop">Clock.Desktop</a></p></td>
<td align="left"><p>.NET 4.8 WPF</p></td>
<td align="left"><p>Десктопное приложение</p></td>
<td align="left"><p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_BuildDesktop">Создание дистрибутива</a></p></td>
<td align="left"><p>дистрибутив Windows</p></td>
</tr>
<tr class="even">
<td align="left"><p><a href="https://github.com/JetBrains/teamcity-dotnet-samples/tree/master/Clock.Desktop.Uwp">Clock.Desktop.Uwp</a></p></td>
<td align="left"><p>UAP</p></td>
<td align="left"><p><a href="https://docs.microsoft.com/en-us/windows/uwp/">UWP</a>-приложение</p></td>
<td align="left"><p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_BuildDesktop">Создание пакета UWP</a></p></td>
<td align="left"><p>пакет UWP</p></td>
</tr>
<tr class="odd">
<td align="left"><p><a href="https://github.com/JetBrains/teamcity-dotnet-samples/tree/master/Clock.Xamarin">Clock.Xamarin</a></p></td>
<td align="left"><p>.NET Standard 1.2</p></td>
<td align="left"><p>Общая библиотека представлений Xamarin</p></td>
<td align="left"><p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_BuildAndroid">Cоздание Android-пакета</a></p></td>
<td align="left"><p>пакет Android</p></td>
</tr>
<tr class="even">
<td align="left"><p><a href="https://github.com/JetBrains/teamcity-dotnet-samples/tree/master/Clock.Xamarin.Android">Clock.Xamarin.Android</a></p></td>
<td align="left"><p>Xamarin Android</p></td>
<td align="left"><p>Мобильное приложение Android</p></td>
<td align="left"><p><a href="https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_BuildAndroid">Cоздание Android-пакета</a></p></td>
<td align="left"><p>пакет Android</p></td>
</tr>
</tbody>
</table>

Для настройки CI/CD-процесса, мы используем иерархию из проектов и [билд-конфигураций](https://www.jetbrains.com/help/teamcity/build-configuration.html). Хотя TeamCity предоставляет дружественный пользовательский интерфейс, все проекты и конфигурации были созданы средствами [TeamCity Kotlin DSL](https://www.jetbrains.com/help/teamcity/kotlin-dsl.html#How+to+Read+Files+in+Kotlin+DSL). Так удобнее версионировать настройки проектов, а затем делиться ими. При желании вы сможете использовать их на своем сервере, избежав ручной настройки. Более подробную информацию о том, как создавать конфигурации через код, используя DSL, можно найти [здесь](https://blog.jetbrains.com/teamcity/2019/03/configuration-as-code-part-1-getting-started-with-kotlin-dsl/) (на английском).

Для запуска конфигураций в туториале мы используем 2 типа агентов:

* Windows 10 x64 Pro 10.0.19041
  * Visual Studio 2019 Pro 16.8.1
    * Docker (Windows container) 19.03.13
    * .NET SDK 5.0
* Ubuntu 16.04
  * Docker 18.06.3

Первым шагом мы создали и разместили на GitHub стандартный проект на [Maven](https://github.com/JetBrains/teamcity-dotnet-samples/tree/master/.teamcity), используя [IntelliJ IDEA](https://www.jetbrains.com/idea/) 2020.2.2 с поддержкой всех его возможностей: подсветкой кода, рефакторингом и т.д. Чтобы сделать поддержку проекта еще более удобной, в его DSL-настройках мы использовали наследование типов Kotlin.

![](https://raw.githubusercontent.com/JetBrains/teamcity-dotnet-samples/master/docs/TeamCity.NET.00.png)

Этот DSL-код нужно подключить к соответствующему [проекту .NET](https://teamcity.jetbrains.com/project.html?projectId=DemoProjects_TeamCity_Net) в TeamCity. Если вы решите разместить проект у себя на сервере, создайте новый проект в TeamCity с [VCS root](https://www.jetbrains.com/help/teamcity/vcs-root.html)’ом, указывающим на наш репозиторий с DSL-настройками. Затем, в секции Versioned Settings, включите синхронизацию настроек с системой версионирования и выберите формат настроек “Kotlin”:

![](https://raw.githubusercontent.com/JetBrains/teamcity-dotnet-samples/master/docs/TeamCity.NET.02.png)

После синхронизации настроек, TeamCity подгрузит DSL-код и создаст вложенные подпроекты и конфигурации из него.

В [корневой проект .NET](https://teamcity.jetbrains.com/project.html?projectId=DemoProjects_TeamCity_Net) входят два подпроекта: «Building» и «Deployment». Они содержат соответствующие их названиям билд-конфигурации.

![](https://raw.githubusercontent.com/JetBrains/teamcity-dotnet-samples/master/docs/TeamCity.NET.01.png)

Помимо них, прямо в корневом проекте .NET лежат две общие билд-конфигурации – Build и Deploy. Первая собирает все приложения и пакеты из подпроектов:

![](https://raw.githubusercontent.com/JetBrains/teamcity-dotnet-samples/master/docs/TeamCity.NET.03.png)

вторая – разворачивает их, в данном случае в репозитории Docker и NuGet:

![](https://raw.githubusercontent.com/JetBrains/teamcity-dotnet-samples/master/docs/TeamCity.NET.04.png)

Все конфигурации сборки объединяет то, что каждая содержит набор [системных параметров](https://docs.microsoft.com/en-us/windows/uwp/packaging/auto-build-package-uwp-apps#configure-the-build-solution-build-task), передаваемых в .NET-команды в виде пар ключ-значение `/p:key=value`. Например, когда в билд-конфигурации определен системный параметр `system.configuration=Release`, то при запуске различных команд им передается параметр `/p:configuration=Release`. Префикс `system.` при этом пропадает, так как в TeamCity он указывает на тип параметра, а за его пределами будет лишним. Все приведенные ниже системные параметры, определенные в наших конфигурациях сборки, не специфичны для TeamCity и описаны в различной документации по .NET:

<table>
  <tr>
   <td></td>
   <td><strong>Параметр</strong></td>
   <td><strong>Значение в конфигурациях TeamCity</strong></td>
   <td><strong>Описание</strong></td>
  </tr>
  <tr>
   <td rowspan="3" ><strong>Все конфигурации сборки</strong></td>
   <td>configuration</td>
   <td>Release</td>
   <td><a href="https://docs.microsoft.com/en-us/visualstudio/msbuild/common-msbuild-project-properties">MSBuild-конфигурация</a>.</td>
  </tr>
  <tr>
   <td>VersionPrefix</td>
   <td>1.0.0</td>
   <td>Используется как<a href="https://andrewlock.net/version-vs-versionsuffix-vs-packageversion-what-do-they-all-mean/#versionprefix"> базовая версия</a> для приложений и пакетов.</td>
  </tr>
  <tr>
   <td>VersionSuffix</td>
   <td>beta%build.number%</td>
   <td>Используется как<a href="https://andrewlock.net/version-vs-versionsuffix-vs-packageversion-what-do-they-all-mean/#versionsuffix"> пререлизная метка</a> в версии для приложений и пакетов.</td>
  </tr>
  <tr>
   <td><strong>Build console and web</strong></td>
   <td>InvariantGlobalization</td>
   <td>true</td>
   <td><a href="https://docs.microsoft.com/en-us/dotnet/core/run-time-config/globalization#invariant-mode">Выполнять приложение без доступа к ресурсам</a>, специфичным культуре.</td>
  </tr>
  <tr>
   <td rowspan="2" ><strong>Build Windows desktop</strong></td>
   <td>PublishDir</td>
   <td>../bin/Clock.Desktop/win/</td>
   <td>Определяет путь публикации приложения.</td>
  </tr>
  <tr>
   <td>AppxPackageDir</td>
   <td>../bin/Clock.Desktop.Uwp/win/</td>
   <td>Определяет<a href="https://docs.microsoft.com/en-us/windows/uwp/packaging/auto-build-package-uwp-apps#configure-the-build-solution-build-task"> путь публикации</a> пакета UWP приложения.</td>
  </tr>
  <tr>
   <td rowspan="5" ><strong>Pack</strong></td>
   <td>Copyright</td>
   <td>Copyright 2020 JetBrains</td>
   <td rowspan="5" ><a href="https://docs.microsoft.com/en-us/dotnet/core/tools/csproj#nuget-metadata-properties">Метаданные</a> NuGet-пакетов.</td>
  </tr>
  <tr>
   <td>Title</td>
   <td>TeamCity .NET sample</td>
  </tr>
  <tr>
   <td>RepositoryType</td>
   <td>git</td>
  </tr>
  <tr>
   <td>RepositoryUrl</td>
   <td>https://github.com/JetBrains/teamcity-dotnet-samples.git</td>
  </tr>
  <tr>
   <td>RepositoryBranch</td>
   <td>refs/heads/master</td>
  </tr>
</table>


Эти параметры можно включить непосредственно в проектные файлы или передавать через параметры командной строки. Лучше использовать [системные параметры](https://docs.microsoft.com/en-us/windows/uwp/packaging/auto-build-package-uwp-apps#configure-the-build-solution-build-task) TeamCity, так как это позволяет не заботиться о деталях передачи специальных символов – TeamCity позаботится об этом сам. Рассмотрим конфигурации тестирования и сборки подробнее.

#### Конфигурации «Test on Windows и Test on Linux»

Две эти конфигурации тестируют общую логику приложений и собирают статистику покрытия кода в Windows агента #1 и в Linux Docker-контейнере [mcr.microsoft.com/dotnet/core/sdk:5.0](https://hub.docker.com/_/microsoft-dotnet-sdk/), используя один шаг сборки .NET. Для сценария Linux Docker, в UI он выглядит так:

![](https://raw.githubusercontent.com/JetBrains/teamcity-dotnet-samples/master/docs/TeamCity.NET.05.png)

В этом сценарии тесты и тестируемые библиотеки собираются и тестируются в Linux [Docker-контейнере](https://hub.docker.com/_/microsoft-dotnet-sdk/) с .NET SDK 5.0. Конфигурация для тестов под Windows отличается лишь тем, что не использует Docker. 

Тестовый проект Clock.Tests является приложением .NET 5.0, поэтому для построения и запуска тестов достаточно одной .NET Core CLI команды `test`. Для сбора и анализа статистики покрытия кода используется кроссплатформенный инструмент для оценки покрытия кода JetBrains dotCover из пакета [JetBrains.dotCover.DotNetCliTool](https://www.nuget.org/packages/JetBrains.dotCover.DotNetCliTool), который устанавливается как [инструмент TeamCity](https://www.jetbrains.com/help/teamcity/installing-agent-tools.html). В DSL, тестовые конфигурации имеют общего предка [TestBase](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L110) и две конфигурации для [тестов на Linux](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L185) и для [тестов на Windows](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L188).

#### Конфигурации «Build console and web for win-x64» и «Build console and web for linux-x64»

Эти две TeamCity конфигурации для [Linux](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L191) и для [Windows](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L194) собирают по два проекта Clock.Console и Clock.Web и состоят из двух шагов сборки, соответствующих этим проектам. Конфигурации наследуются от [BuildConsoleAndWebBase](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L137), который в свою очередь наследуется от базового типа для всех конфигураций сборки приложений – [BuildBase](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L74). Оба шага можно было бы объединить в один, указав в [Projects](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L168) сразу два проекта, но в этом случае было бы сложно разделить бинарные файлы от разных приложений, как это сейчас сделано через [outputDir](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L170). Так как оба приложения имеют тип .NET 5.0, как и в предыдущем случае, для построения и публикации достаточно одной .NET Core CLI команды `publish`. 

Вот как выглядит [первый шаг](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L166) в UI для Linux, который строит и публикует приложение Clock.Console в виде единственного запускаемого файла в папку, определенную в поле _Output directory_:

![](https://raw.githubusercontent.com/JetBrains/teamcity-dotnet-samples/master/docs/TeamCity.NET.06.png)

Для Windows, данный шаг отличается полем [Runtime](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L169), в котором определено значение _win-x64_, и полем _Output directory_ со значением _bin/Clock.Console/win-x64_.

[Второй шаг](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L172) для построения и публикации приложения Clock.Web для Linux отличается от первого шага, помимо пути к проектному файлу, полем _Output directory_ со значением _bin/Clock.Web/linux-x64_. После завершения шагов, TeamCity [публикует](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L180) построенные приложения как артефакты билда.

Для _win-x64_:
* bin/Clock.Console/win-x64/Clock.Console
* bin/Clock.Console/win-x64/Clock.Web

Для _linux-x64_:
  * bin/Clock.Console/linux-x64/Clock.Console
  * bin/Clock.Console/linux-x64/Clock.Web

#### Конфигурация «Build Windows desktop»

В этой конфигурации всего [один шаг](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L215):

![](https://raw.githubusercontent.com/JetBrains/teamcity-dotnet-samples/master/docs/TeamCity.NET.07.png)

На этом шаге запускается Windows MSBuild из Visual Studio 2019 на агенте #1 для выполнения «таргетов» Restore, Rebuild и Publish последовательно для каждого из двух проектов:

* Clock.Desktop/Clock.Desktop.csproj
* Clock.Desktop.Uwp/Clock.Desktop.csproj

Результаты сборок публикуются в разные директории, определенные в системных параметрах конфигураций PublishDir (для Clock.Desktop) и AppxPackageDir (для Clock.Desktop.Uwp). Эти директории далее публикуются как артефакты билда.

#### Конфигурация «Build Android app»

[Эта конфигурация](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L233) строит мобильное приложение Android, используя Windows MSBuild из Visual Studio 2019, на агенте #1:

![](https://raw.githubusercontent.com/JetBrains/teamcity-dotnet-samples/master/docs/TeamCity.NET.08.png)

Она аналогична предыдущей конфигурации, но вместо MSBuild «таргета» Publish здесь используется «таргет» SignAndroidPackage, чтобы опубликовать подписанный Android-пакет.

#### Конфигурация «Pack»

[Эта конфигурация](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L256) создает два NuGet-пакета и содержит один шаг .NET CLI с командой `pack`, выполненной последовательно для двух проектов – Clock и Clock.IoC:

![](https://raw.githubusercontent.com/JetBrains/teamcity-dotnet-samples/master/docs/TeamCity.NET.09.png)

Такие метаданные для NuGet-пакетов как название, тип репозитория и т.д., определены через [системные параметры](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L267) конфигурации TeamCity – их список есть в таблице выше. После успешного выполнения этой конфигурации, [в артефактах](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L282) появляются NuGet-пакеты, готовые для отправки в репозиторий на этапе развертывания.

#### Конфигурация «Build»

Эта особая [конфигурация](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L288) TeamCity не содержит шагов сборки. Она предназначена для того, чтобы собрать все артефакты приложений и пакетов в один TeamCity-билд через [артефакт-зависимости](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L296) на остальные конфигурации сборки:

![](https://raw.githubusercontent.com/JetBrains/teamcity-dotnet-samples/master/docs/TeamCity.NET.10.png)

#### Конфигурация «Deploy»

Это [похожая конфигурация](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L506) для развертывания, которая не содержит шагов, а только [зависимости](https://github.com/JetBrains/teamcity-dotnet-samples/blob/dcf1430a234193455b51758deb34c7f14c7496cc/.teamcity/settings.kts#L510) на другие конфигурации:

* _Push image …_ строит и публикует образы для Linux и Windows
* _Push multi-arch image …_ создаёт и публикует манифесты для [мульти-архитектурных образов Docker](https://www.docker.com/blog/multi-arch-build-and-images-the-simple-way/)<span style="text-decoration:underline;">.</span>
* _Publish to NuGet_ публикует ранее созданные NuGet пакеты в [TeamCity NuGet](https://www.jetbrains.com/help/teamcity/using-teamcity-as-nuget-feed.html).

Чтобы подключить опубликованные NuGet-пакеты с общей логикой к проектам, можно использовать [этот](https://teamcity.jetbrains.com/guestAuth/app/nuget/feed/DemoProjects_TeamCity/clock/v2) NuGet-источник. Для запуска приложений Clock.Console в Docker используйте команды:

```
docker pull nikolayp/clock-console
docker run -it --rm nikolayp/clock-console
```

А для Clock.Web:

```
docker pull nikolayp/clock-web
docker run -it --rm -p 5000:5000 nikolayp/clock-web
```

После запуска веб-приложения в контейнере, оно будет доступно по адресу [http://localhost:5000/](http://localhost:5000/).

Все приложения и пакеты можно посмотреть в артефактах [здесь](https://teamcity.jetbrains.com/viewType.html?buildTypeId=DemoProjects_TeamCity_Net_Build).

## Заключение

Microsoft активно развивает [.NET 5](https://dotnet.microsoft.com/download/dotnet/5.0), ведь он предоставляет разработчикам свободный и единый инструментарий. Мы в TeamCity верим, что наш обновленный ранер .NET позволит сделать непрерывную интеграцию проектов на .NET 5 удобной и предсказуемой.

Если вы уже пробовали новый ранер или просто хотите поделиться мнением о нем, пишите в комментариях. Всегда рады вашей обратной связи.