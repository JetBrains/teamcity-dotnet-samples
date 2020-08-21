namespace Clock.Desktop
{
    using System.Collections.Generic;
    using global::IoC;
    using ViewModels;
    using Views;
    using static global::IoC.Lifetime;

    /// <summary>
    /// IoC Configuration.
    /// </summary>
    internal class AppConfiguration: IConfiguration
    {
        public IEnumerable<IToken> Apply(IMutableContainer container)
        {
            yield return container
                .Bind<IDispatcher>().As(Singleton).To<Dispatcher>()
                .Bind<IMainWindowView>().As(Singleton).To<MainWindow>();
        }
    }
}
