// ReSharper disable InconsistentNaming
namespace Clock.Desktop
{
    using System;
    using System.Windows;
    using ViewModels;

    // ReSharper disable once ClassNeverInstantiated.Global
    internal class Dispatcher: IDispatcher
    {
        public void Dispatch(Action action) => Application.Current?.Dispatcher?.Invoke(action);
    }
}
