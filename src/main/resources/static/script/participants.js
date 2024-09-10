import {Utils} from './utils.js';

function filterParticipants(filter) {
    let rows = document.getElementById('participantTable').getElementsByTagName('tr');
    for (let i = 0; i < rows.length; i++) {
        let presence = rows[i].getAttribute('data-presence');
        if (filter === 'all') {
            rows[i].classList.remove('hidden');
        } else if (filter === 'true' && presence === 'true') {
            rows[i].classList.remove('hidden');
        } else if (filter === 'false' && presence === 'false') {
            rows[i].classList.remove('hidden');
        } else {
            rows[i].classList.add('hidden');
        }
    }
}

document.getElementById('showAll').addEventListener('click', function() {
    filterParticipants('all');
});

document.getElementById('showPresent').addEventListener('click', function() {
    filterParticipants('true');
});

document.getElementById('showAbsent').addEventListener('click', function() {
    filterParticipants('false');
});

document.getElementById('myInput').addEventListener('keyup', function() {
    searchAll();
});

function searchAll() {
    let input, filter, table, tr, td, i, txtValue;
    input = Utils.normalizeWhitespace((Utils.sanitizeInput(document.getElementById("myInput")).value.trim()));
    document.getElementById("myInput").value = input;
    filter = input.value.toUpperCase();
    table = document.getElementById("our-table");
    tr = table.getElementsByTagName("tr");
    for (i = 0; i < tr.length; i++) {
        let rowText = "";
        td = tr[i].getElementsByTagName("td");
        for (let j = 0; j < td.length; j++) {
            rowText += td[j].textContent || td[j].innerText;
        }
        if (rowText.toUpperCase().indexOf(filter) > -1) {
            tr[i].style.display = "";
        } else {
            tr[i].style.display = "none";
        }
    }
}

