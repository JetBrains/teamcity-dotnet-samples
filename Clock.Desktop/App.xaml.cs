namespace Clock.Desktop
{
    using System.Windows;
    using global::IoC;
    using IoC;
    using Views;
    using static global::IoC.Container;

    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App
    {
        internal readonly IMutableContainer Container =
            Create()
            .Using<ClockConfiguration>()
            .Using<AppConfiguration>();

        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);
            Container.Resolve<IMainWindowView>().Show();
        }

        protected override void OnExit(ExitEventArgs e)
        {
            Container.Dispose();
            base.OnExit(e);
        }
    }
}