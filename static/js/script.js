(function () {
  "use strict";

  // Live clock in the footer
  var clocks = document.querySelectorAll(".clock");
  function tick() {
    var now = new Date();
    var pad = function (n) {
      return String(n).padStart(2, "0");
    };
    var time = pad(now.getHours()) + ":" + pad(now.getMinutes()) + ":" + pad(now.getSeconds());
    clocks.forEach(function (el) {
      el.textContent = time;
    });
  }
  if (clocks.length) {
    tick();
    setInterval(tick, 1000);
  }

  // Fake form handler — replace with a real endpoint
  var forms = document.querySelectorAll("form.form");
  forms.forEach(function (form) {
    form.addEventListener("submit", function (e) {
      e.preventDefault();
      var btn = form.querySelector("button");
      btn.textContent = "Signal sent ✓";
      btn.disabled = true;
      setTimeout(function () {
        form.reset();
        btn.textContent = "Transmit →";
        btn.disabled = false;
      }, 2400);
    });
  });
})();
