document.addEventListener('DOMContentLoaded', function() {
    const divs = document.querySelectorAll('.right-effect');
    let index = 0;

    let divv = document.getElementsByClassName("hidden-text-1")[0];

    let wdth = window.innerWidth;
    setTimeout(() => {
        divv.classList.add("mostra1");
    }, 1000);

    function showNextDiv() {
        if (index < divs.length) {
            divs[index].classList.remove('hidden-vis');
            divs[index].classList.add('visible');
            index++;

            setTimeout(showNextDiv, 1000);
        }
    }

    setTimeout(showNextDiv, 3000);

    let div = document.getElementsByClassName("hidden-button")[0];
    setTimeout(() => {
        div.classList.add("mostra");
    }, wdth >= 1200 ? 7000 : 2000);

    setTimeout(function() {
        let text = "Be ready for the new exclusive party.";
        let index = 0;
        let speed = 80;

        function typing() {
            if (index < text.length) {
                document.getElementById("typewriterText").innerHTML += text.charAt(index);
                index++;
                setTimeout(typing, speed);
            }
        }

        typing();
    }, wdth >= 1200 ? 8000 : 3000);
});
