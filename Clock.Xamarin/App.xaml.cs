namespace Clock.Xamarin
{
    using Clock;
    using global::IoC;
    using global::Xamarin.Forms;
    using IoC;
    using static global::IoC.Container;

    public partial class App
    {
        internal readonly IMutableContainer Container =
            Create()
            .Using<ClockConfiguration>()
            .Using<AppConfiguration>();

        public App()
        {
            InitializeComponent();
            MainPage = Container.Resolve<Page>();
        }

        protected override void CleanUp()
        {
            Container.Dispose();
            base.CleanUp();
        }
    }
}
