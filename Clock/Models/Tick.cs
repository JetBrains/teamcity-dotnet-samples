namespace Clock.Models
{
    [ExcludeFromCodeCoverage]
    public class Tick
    {
        public static readonly Tick Shared = new Tick();

        private Tick() { }
    }
}
