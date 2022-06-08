function deviceType() {
    const ua = navigator.userAgent;
    if (/(tablet|ipad|playbook|silk)|(android(?!.*mobi))/i.test(ua)) {
        return "tablet";
    } else if (/Mobile|Android|iP(hone|od)|IEMobile|BlackBerry|Kindle|Silk-Accelerated|(hpw|web)OS|Opera M(obi|ini)/.test(ua)) {
        return "mobile";
    }
    return "desktop";

}

console.log(deviceType());

const sideMenu = document.querySelector("aside");
const menuBtn = document.querySelector("#menu-btn");
const closeBtn = document.querySelector("#close-btn");

menuBtn.addEventListener('click', () => {
    sideMenu.style.display = 'block';
});

closeBtn.addEventListener('click', () => {
    sideMenu.style.display = 'none';
});


const input = document.querySelector("#input");

input.addEventListener('input', () => {
    const cameras = document.getElementsByClassName('camera');
    for (var i = 0; i < cameras.length; i++) {
        if (!(cameras[i].id.toLowerCase().includes(input.value))) {
            cameras[i].style.display = 'none';
        } else {
            cameras[i].style.display = 'block';
        }
    }
});

const openModalButtons = document.querySelectorAll('[data-modal-target]')
const closeModalButtons = document.querySelectorAll('[data-close-button]');
const overlay = document.getElementById('overlay');

openModalButtons.forEach(button => {
    button.addEventListener('click', () => {
        const modal = document.querySelector(button.dataset.modalTarget);
        openModal(modal);
    });
});

overlay.addEventListener('click', () => {
    const modals = document.querySelectorAll('.modal.active');
    modals.forEach(modal => {
        closeModal(modal);
    });
});

closeModalButtons.forEach(button => {
    button.addEventListener('click', () => {
        const modal = button.closest('.modal');
        closeModal(modal);
    })
});

function openModal(modal) {
    if (modal == null) return
    modal.classList.add('active');
    overlay.classList.add('active');
}

function closeModal(modal) {
    if (modal == null) return
    modal.classList.remove('active');
    overlay.classList.remove('active');
}
var light = true;
const themeToggler = document.querySelector('.theme-Toggler');
themeToggler.addEventListener('click', () => {
    document.body.classList.toggle('dark-theme');
    light = !light;
    let newInner = `<span
					class="material-icons">light_mode</span>
					<h3>Toggle Mode</h3>`;
    if (light) {
        newInner = `<span
					class="material-icons">dark_mode</span>
					<h3>Toggle Mode</h3>`;
    }
    themeToggler.innerHTML = newInner;
});

window.addEventListener('resize', () => {
    if (window.outerWidth >= 769) {
        sideMenu.style.display = 'block';
    }
});