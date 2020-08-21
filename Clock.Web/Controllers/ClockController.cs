namespace Clock.Web.Controllers
{
    using System.Collections.Generic;
    using Microsoft.AspNetCore.Mvc;
    using ViewModels;

    [ApiController]
    [Route("api/[controller]")]
    public class ClockController : ControllerBase
    {
        private readonly IClockViewModel _viewModel;

        public ClockController(IClockViewModel viewModel) => _viewModel = viewModel;

        [HttpGet]
        public IEnumerable<string> Get()
        {
            yield return _viewModel.Date;
            yield return _viewModel.Time;
        }
    }
}
