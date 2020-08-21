namespace Clock.Models
{
    using System;

    // ReSharper disable once ClassNeverInstantiated.Global
    [ExcludeFromCodeCoverage]
    internal class Clock : IClock
    {
        public DateTimeOffset Now => DateTimeOffset.Now;
    }
}