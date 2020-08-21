namespace Clock.Xamarin
{
    using System.Collections.Generic;
    using global::IoC;
    using global::Xamarin.Forms;
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
                .Bind<Page>().As(Singleton).To<MainPage>();
        }
    }
}
