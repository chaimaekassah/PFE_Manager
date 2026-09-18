/**
 * suivi-projet.js
 * Client-side search & filter for the Encadrant Suivi-Projet page.
 * No external dependencies required.
 */

function filterProjects(query) {
    var q = (query || '').toLowerCase().trim();
    var sel = document.getElementById('sp-filter-status');
    var s = sel ? sel.value.toLowerCase() : '';
    applyFilters(q, s);
}

function filterByStatus(statusVal) {
    var inp = document.getElementById('sp-search');
    var q = inp ? inp.value.toLowerCase().trim() : '';
    applyFilters(q, statusVal.toLowerCase());
}

function applyFilters(q, s) {
    var table = document.getElementById('sp-projects-table');
    if (!table) { return; }
    var rows = table.querySelectorAll('tbody tr');
    rows.forEach(function (row) {
        var sujet    = (row.getAttribute('data-sujet')    || '').toLowerCase();
        var etudiant = (row.getAttribute('data-etudiant') || '').toLowerCase();
        var statut   = (row.getAttribute('data-statut')   || '').toLowerCase();
        var matchQ = !q || sujet.indexOf(q) !== -1 || etudiant.indexOf(q) !== -1;
        var matchS = !s || statut.indexOf(s) !== -1;
        row.style.display = (matchQ && matchS) ? '' : 'none';
    });
}
