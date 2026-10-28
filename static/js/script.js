(function () {
  "use strict";

  var themeControl = document.querySelector(".theme-control");
  var themeTrigger = document.querySelector(".theme-trigger");
  var themeMenu = document.getElementById("theme-menu");
  if (themeControl && themeTrigger && themeMenu) {
    var themes = ["soft", "warm", "original", "light"];
    var activeTheme = document.documentElement.dataset.theme;
    var themeOptions = themeMenu.querySelectorAll(".theme-option");
    function setSelected(theme) {
      themeOptions.forEach(function (option) {
        option.setAttribute("aria-pressed", String(option.dataset.themeOption === theme));
      });
    }
    function closeMenu(restoreFocus) {
      themeMenu.hidden = true;
      themeTrigger.setAttribute("aria-expanded", "false");
      if (restoreFocus) themeTrigger.focus();
    }
    setSelected(themes.indexOf(activeTheme) >= 0 ? activeTheme : "soft");
    themeControl.hidden = false;
    themeTrigger.addEventListener("click", function () {
      var opening = themeMenu.hidden;
      themeMenu.hidden = !opening;
      themeTrigger.setAttribute("aria-expanded", String(opening));
    });
    themeOptions.forEach(function (option) {
      option.addEventListener("click", function () {
        var choice = option.dataset.themeOption;
        if (themes.indexOf(choice) < 0) return;
        document.documentElement.dataset.theme = choice;
        setSelected(choice);
        try { localStorage.setItem("site-theme", choice); } catch (e) { /* The selection still works for this page. */ }
        closeMenu(true);
      });
    });
    document.addEventListener("click", function (event) {
      if (!themeMenu.hidden && !themeControl.contains(event.target)) closeMenu(false);
    });
    document.addEventListener("keydown", function (event) {
      if (event.key === "Escape" && !themeMenu.hidden) closeMenu(true);
    });
    themeControl.addEventListener("focusout", function (event) {
      if (event.relatedTarget && !themeControl.contains(event.relatedTarget)) closeMenu(false);
    });
  }

})();
