/* japicmp report: adds search, filters and expand/collapse. The report is complete without it. */
(function () {
	"use strict";
	var toolbar = document.getElementById("japicmp-toolbar");
	var classesRoot = document.getElementById("japicmp-classes");
	if (!toolbar || !classesRoot) {
		return;
	}
	toolbar.hidden = false;
	var root = (classesRoot.closest && classesRoot.closest(".japicmp")) || document.body;

	var classes = Array.prototype.slice.call(classesRoot.querySelectorAll("details.cls"));
	var buttons = Array.prototype.slice.call(toolbar.querySelectorAll(".seg button"));
	var search = document.getElementById("japicmp-q");
	var expandAll = document.getElementById("japicmp-expandAll");
	var collapseAll = document.getElementById("japicmp-collapseAll");
	var result = document.getElementById("japicmp-result");
	var empty = document.getElementById("japicmp-empty");
	var filter = "all";
	var query = "";
	var userOpen = {};

	function rowMatches(row, classNameHit) {
		var sev = row.getAttribute("data-sev");
		var status = row.getAttribute("data-status");
		if (filter === "breaking" && sev !== "bin" && sev !== "src") {
			return false;
		}
		if ((filter === "NEW" || filter === "REMOVED" || filter === "MODIFIED") && status !== filter) {
			return false;
		}
		return !query || classNameHit || row.getAttribute("data-q").indexOf(query) >= 0;
	}

	function apply() {
		var active = filter !== "all" || query !== "";
		var shownClasses = 0;
		var shownRows = 0;
		classes.forEach(function (cls) {
			var classNameHit = query !== "" && cls.getAttribute("data-q").indexOf(query) >= 0;
			var visible = 0;
			Array.prototype.forEach.call(cls.querySelectorAll("section.group"), function (group) {
				var groupVisible = 0;
				Array.prototype.forEach.call(group.querySelectorAll("article.change"), function (row) {
					var match = rowMatches(row, classNameHit);
					row.hidden = !match;
					if (match) {
						groupVisible++;
					}
				});
				group.hidden = groupVisible === 0;
				var count = group.querySelector(".gcount");
				if (count) {
					count.textContent = groupVisible;
				}
				visible += groupVisible;
			});
			var keep = visible > 0 || (filter === "all" && (query === "" || classNameHit));
			cls.hidden = !keep;
			if (keep) {
				shownClasses++;
				shownRows += visible;
				if (active) {
					cls.open = visible > 0;
				} else {
					cls.open = !!userOpen[cls.id];
				}
			}
		});
		empty.hidden = shownClasses > 0;
		result.textContent = shownRows + " changes in " + shownClasses + " classes";
		updateButtons();
	}

	function updateButtons() {
		var visible = classes.filter(function (c) { return !c.hidden; });
		expandAll.disabled = visible.length === 0 || visible.every(function (c) { return c.open; });
		collapseAll.disabled = visible.length === 0 || visible.every(function (c) { return !c.open; });
	}

	function setFilter(value) {
		filter = value;
		buttons.forEach(function (b) { b.setAttribute("aria-pressed", String(b.getAttribute("data-f") === value)); });
	}

	function reset() {
		if (filter !== "all" || query !== "") {
			setFilter("all");
			query = "";
			search.value = "";
			apply();
		}
	}

	function reveal(id) {
		var el = id ? document.getElementById(id) : null;
		if (!el) {
			return;
		}
		if (el.hidden || (el.closest && el.closest("[hidden]"))) {
			reset();
		}
		var cls = el.closest ? el.closest("details.cls") : null;
		if (cls && !cls.open) {
			cls.open = true;
			userOpen[cls.id] = true;
		}
		el.scrollIntoView({block: "start"});
		updateButtons();
	}

	buttons.forEach(function (b) {
		b.addEventListener("click", function () {
			setFilter(b.getAttribute("data-f"));
			apply();
		});
	});
	search.addEventListener("input", function () {
		query = search.value.trim().toLowerCase();
		apply();
	});
	function setAll(open) {
		classes.forEach(function (c) {
			if (!c.hidden) {
				c.open = open;
				userOpen[c.id] = open;
			}
		});
		updateButtons();
	}
	expandAll.addEventListener("click", function () { setAll(true); });
	collapseAll.addEventListener("click", function () { setAll(false); });
	classesRoot.addEventListener("toggle", function (e) {
		var target = e.target;
		if (target.classList && target.classList.contains("cls")) {
			if (filter === "all" && query === "") {
				userOpen[target.id] = target.open;
			}
			updateButtons();
		}
	}, true);
	document.addEventListener("click", function (e) {
		var link = e.target.closest ? e.target.closest("a[href^='#']") : null;
		// only links of the report itself, the report may be embedded into a Maven site
		if (link && root.contains(link) && link.getAttribute("href").length > 1) {
			var id = decodeURIComponent(link.getAttribute("href").slice(1));
			if (document.getElementById(id)) {
				e.preventDefault();
				if (history.replaceState) {
					history.replaceState(null, "", "#" + id);
				}
				reveal(id);
			}
		}
	});
	window.addEventListener("hashchange", function () { reveal(decodeURIComponent(location.hash.slice(1))); });

	apply();
	if (location.hash.length > 1) {
		reveal(decodeURIComponent(location.hash.slice(1)));
	}
})();
