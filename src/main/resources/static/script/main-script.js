document.querySelectorAll('.dropdown-toggle').forEach((element) => {
    element.addEventListener('mouseover', () => {
        const displayElements = document.querySelectorAll('.dropdown-menu');
        displayElements.forEach((displayElement) => {
            let isHovered = false;

            displayElement.style.display = 'flex';

            if (window.innerWidth < 992) {
                displayElement.style.flexDirection = 'row';
            } else {
                displayElement.style.flexDirection = 'column';
            }

            displayElement.addEventListener('mouseenter', () => {
                isHovered = true;
            });

            displayElement.addEventListener('mouseleave', () => {
                isHovered = false;
                displayElement.style.display = 'none';
            });

            setTimeout(() => {
                if (!isHovered) {
                    displayElement.style.display = 'none';
                }
            }, 2000);
        });
    });
});
document.querySelectorAll('.dropdown').forEach((button) => {
    button.addEventListener('click', (event) => {
        const displayElements = document.querySelectorAll('.dropdown-menu');
        displayElements.forEach((displayElement) => {
            displayElement.style.display = 'flex';
            if (window.innerWidth < 992) {
                displayElement.style.flexDirection = 'row';
            } else {
                displayElement.style.flexDirection = 'column';
            }
            setTimeout(() => {
                displayElement.style.display = 'none';
            }, 5000);
        });
    });
});
