﻿// ReSharper disable MemberCanBePrivate.Global
// ReSharper disable UnusedAutoPropertyAccessor.Global
namespace Clock.Desktop
{
    using System.Windows;
    using System.Windows.Data;
    using global::IoC;
    using IoC;

    public class DataProvider: ObjectDataProvider
    {
        private readonly IContainer _container =
            (Application.Current as App)?.Container
            // Resolves from Design Time Container
            ?? Container.Create().Using<ClockDesignTimeConfiguration>();

        public object Tag { get; set; }

        protected override void BeginQuery() => OnQueryFinished(_container.Resolve<object>(ObjectType, Tag));
    }
}
