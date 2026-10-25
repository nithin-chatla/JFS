// ==========================================================================
// CampusStay HMS - Enterprise Client Interactions & Form Enhancements
// ==========================================================================

document.addEventListener('DOMContentLoaded', () => {
    // 1. Password Visibility Toggle
    const togglePasswordBtn = document.getElementById('togglePasswordBtn');
    if (togglePasswordBtn) {
        togglePasswordBtn.addEventListener('click', (e) => {
            e.preventDefault();
            const passwordInput = document.getElementById('password');
            const icon = document.getElementById('togglePasswordIcon');
            if (passwordInput && icon) {
                if (passwordInput.type === 'password') {
                    passwordInput.type = 'text';
                    icon.classList.remove('bi-eye');
                    icon.classList.add('bi-eye-slash');
                } else {
                    passwordInput.type = 'password';
                    icon.classList.remove('bi-eye-slash');
                    icon.classList.add('bi-eye');
                }
            }
        });
    }

    const toggleConfirmBtn = document.getElementById('toggleConfirmPasswordBtn');
    if (toggleConfirmBtn) {
        toggleConfirmBtn.addEventListener('click', (e) => {
            e.preventDefault();
            const confirmInput = document.getElementById('confirmPassword');
            const icon = document.getElementById('toggleConfirmIcon');
            if (confirmInput && icon) {
                if (confirmInput.type === 'password') {
                    confirmInput.type = 'text';
                    icon.classList.remove('bi-eye');
                    icon.classList.add('bi-eye-slash');
                } else {
                    confirmInput.type = 'password';
                    icon.classList.remove('bi-eye-slash');
                    icon.classList.add('bi-eye');
                }
            }
        });
    }

    // 2. Real-time Password Match Feedback on Registration Page
    const regPassword = document.getElementById('regPassword');
    const confirmPassword = document.getElementById('confirmPassword');
    const passwordMatchMsg = document.getElementById('passwordMatchMessage');

    function checkPasswordMatch() {
        if (!regPassword || !confirmPassword || !passwordMatchMsg) return;
        const pass = regPassword.value;
        const conf = confirmPassword.value;

        if (conf.length === 0) {
            passwordMatchMsg.textContent = '';
            passwordMatchMsg.style.display = 'none';
            return;
        }

        passwordMatchMsg.style.display = 'block';
        if (pass === conf) {
            passwordMatchMsg.innerHTML = '<i class="bi bi-check-circle-fill"></i> Passwords match perfectly';
            passwordMatchMsg.className = 'form-text-success';
            confirmPassword.style.borderColor = 'var(--success)';
        } else {
            passwordMatchMsg.innerHTML = '<i class="bi bi-x-circle-fill"></i> Passwords do not match';
            passwordMatchMsg.className = 'form-text-error';
            confirmPassword.style.borderColor = 'var(--danger)';
        }
    }

    if (regPassword && confirmPassword) {
        regPassword.addEventListener('input', checkPasswordMatch);
        confirmPassword.addEventListener('input', checkPasswordMatch);
    }

    // 3. Client-side Instant Table Search Filter
    const searchInputs = document.querySelectorAll('#tableSearchInput, .live-table-search');
    searchInputs.forEach(input => {
        input.addEventListener('input', () => {
            const filter = input.value.toLowerCase().trim();
            const table = input.closest('.card-custom') 
                ? input.closest('.card-custom').querySelector('table') 
                : document.querySelector('.table-custom');

            if (!table) return;

            const rows = table.querySelectorAll('tbody tr');
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(filter) ? '' : 'none';
            });
        });
    });

    // 4. Auto-dismiss alerts after 6 seconds with smooth fade-out
    const alerts = document.querySelectorAll('.alert-custom');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            alert.style.transition = 'all 0.5s ease';
            setTimeout(() => alert.remove(), 500);
        }, 6000);
    });

    // 5. Interactive Star Rating Hover
    const starLabels = document.querySelectorAll('.star-rating-select label');
    starLabels.forEach((star) => {
        star.addEventListener('mouseenter', () => {
            star.style.transform = 'scale(1.25)';
        });
        star.addEventListener('mouseleave', () => {
            star.style.transform = '';
        });
    });
});
