namespace Clock.Console
{
    using System;
    using System.ComponentModel;
    using global::IoC;
    using IoC;
    using ViewModels;
    using static global::IoC.Container;
    using static System.Console;

    // ReSharper disable once ClassNeverInstantiated.Global
    public class Program: IDisposable
    {
        public static void Main()
        {
            using var compositionRoot = 
                Create()
                .Using<ClockConfiguration>()
                .BuildUp<Program>();

            compositionRoot.Instance.Run();
        }

        private readonly IClockViewModel _clockViewModel;

        internal Program(IClockViewModel clockViewModel)
        {
            _clockViewModel = clockViewModel;
            ((INotifyPropertyChanged)_clockViewModel).PropertyChanged += OnPropertyChanged;
        }

        private void Run() => ReadLine();

        private void OnPropertyChanged(object sender, PropertyChangedEventArgs eventArgs)
        {
            switch (eventArgs.PropertyName)
            {
                case nameof(IClockViewModel.Date):
                    WriteLine($"{eventArgs.PropertyName}: {_clockViewModel.Date}");
                    break;

                case nameof(IClockViewModel.Time):
                    WriteLine($"{eventArgs.PropertyName}: {_clockViewModel.Time}");
                    break;
            }
        }

        public void Dispose() => ((INotifyPropertyChanged)_clockViewModel).PropertyChanged -= OnPropertyChanged;
    }
}
