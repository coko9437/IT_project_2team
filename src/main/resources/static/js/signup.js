document.addEventListener('DOMContentLoaded', () => {
    // --- DOM 요소 선택 ---
    const signupForm = document.getElementById('signupForm');
    const emailInput = document.getElementById('email');
    const sendAuthCodeBtn = document.getElementById('sendAuthCodeBtn');
    const emailAuthCodeInput = document.getElementById('emailAuthCode');
    const verifyAuthCodeBtn = document.getElementById('verifyAuthCodeBtn');
    const signupBtn = document.getElementById('signupBtn');
    const profileImageInput = document.getElementById('profileImage');
    const imagePreview = document.getElementById('imagePreview');

    // 모달 관련 요소
    const modal = document.getElementById('myModal');
    const modalMessage = document.getElementById('modalMessage');
    const modalConfirmBtn = document.getElementById('modalConfirmBtn');

    // 입력 필드와 에러/성공 메시지 매핑
    const fields = {
        name: { input: document.getElementById('name'), error: document.getElementById('nameError'), success: document.getElementById('nameSuccess') },
        email: { input: emailInput, error: document.getElementById('emailError'), success: document.getElementById('emailSendSuccess') },
        emailAuthCode: { input: emailAuthCodeInput, error: document.getElementById('emailAuthCodeError'), success: document.getElementById('emailVerifySuccess') },
        username: { input: document.getElementById('username'), error: document.getElementById('usernameError'), success: document.getElementById('usernameSuccess')  },
        password: { input: document.getElementById('password'), error: document.getElementById('passwordError'), success: document.getElementById('passwordSuccess')  },
        confirmPassword: { input: document.getElementById('confirmPassword'), error: document.getElementById('confirmPasswordError'), success: document.getElementById('confirmPasswordSuccess')  },
        nickname: { input: document.getElementById('nickname'), error: document.getElementById('nicknameError'), success: document.getElementById('nicknameSuccess')  },
        birthDate: { input: document.getElementById('birthDate'), error: document.getElementById('birthDateError'), success: document.getElementById('birthDateSuccess')  },
        phoneNumber: { input: document.getElementById('phoneNumber'), error: document.getElementById('phoneNumberError'), success: document.getElementById('phoneNumberSuccess')  }
    };

    let isEmailVerified = false; // 이메일 인증 상태 플래그

    // --- 초기 UI 상태 설정 ---
    // 이메일 인증 전까지 회원가입 필드 비활성화
    Object.keys(fields).forEach(key => {
        if (key !== 'email' && key !== 'emailAuthCode') {
            fields[key].input.disabled = true;
        }
    });
    profileImageInput.disabled = true;
    signupBtn.disabled = true;
    imagePreview.style.display = 'none'; // 초기에는 이미지 미리보기 숨김

    // --- 헬퍼 함수: 메시지 표시/숨김 ---
    function showMessage(element, message, type = 'error') {
        if (type === 'error') {
            element.classList.add('error-message');
            element.classList.remove('success-message');
        } else { // type === 'success'
            element.classList.add('success-message');
            element.classList.remove('error-message');
        }
        element.textContent = message;
        element.style.display = 'block';
    }

    function hideMessage(element) {
        // element가 null 또는 undefined가 아닌 경우에만 속성을 조작
        if (element) { // 이 조건문이 signup.js:56 이전에 위치해야 함
            element.textContent = '';
            element.style.display = 'none';
        } else {
            // 어떤 요소가 undefined로 넘어왔는지 콘솔에 경고를 출력하여 디버깅에 도움을 줍니다.
            console.warn("Warning: Attempted to hide a non-existent element (element was null or undefined).");
        }
    }

    function clearAllErrors() {
        Object.values(fields).forEach(field => {
            hideMessage(field.error);
            hideMessage(field.success);
        });
    }

    // --- 3. 프로필 이미지 썸네일 미리보기 ---
    profileImageInput.addEventListener('change', (event) => {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                imagePreview.src = e.target.result;
                imagePreview.style.display = 'block';
            };
            reader.readAsDataURL(file);
        } else {
            imagePreview.src = '';
            imagePreview.style.display = 'none';
        }
    });

    // --- 5. 생년월일 필드 유효성 검사 (type="date"가 브라우저별로 다를 수 있으므로) ---
    fields.birthDate.input.addEventListener('input', (event) => {
        const value = event.target.value;
        const pattern = /^\d{4}-\d{2}-\d{2}$/;
        if (value && !pattern.test(value)) {
            showMessage(fields.birthDate.error, "날짜 형식을 YYYY-MM-DD로 입력해주세요.");
        } else {
            hideMessage(fields.birthDate.error);
        }
    });

    // --- 이메일 인증 로직 ---
    sendAuthCodeBtn.addEventListener('click', async () => {
        clearAllErrors();
        const email = emailInput.value;
        if (!email) {
            showMessage(fields.email.error, "이메일을 입력해주세요.");
            return;
        }

        try {
            const response = await fetch('/api/auth/send-code', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email })
            });

            if (response.ok) {
                showMessage(fields.email.success, "인증번호가 전송되었습니다. 5분 이내에 입력해주세요.", 'success');
                emailAuthCodeInput.disabled = false;
                verifyAuthCodeBtn.disabled = false;
                sendAuthCodeBtn.disabled = true; // 재전송 방지
                emailInput.disabled = true; // 이메일 수정 방지
            } else {
                const errorResult = await response.text(); // 오류 메시지를 텍스트로 받음
                showMessage(fields.email.error, errorResult || "인증번호 전송에 실패했습니다.");
            }
        } catch (error) {
            console.error('인증번호 전송 오류:', error);
            showMessage(fields.email.error, "네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    });

    verifyAuthCodeBtn.addEventListener('click', async () => {
        clearAllErrors();
        const email = emailInput.value;
        const authCode = emailAuthCodeInput.value;

        if (!email || !authCode) {
            showMessage(fields.emailAuthCode.error, "이메일과 인증번호를 모두 입력해주세요.");
            return;
        }

        try {
            const response = await fetch('/api/auth/verify-code', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email, authCode: authCode })
            });

            if (response.ok) {
                isEmailVerified = true;
                showMessage(fields.emailAuthCode.success, "이메일 인증이 완료되었습니다!", 'success');
                emailAuthCodeInput.disabled = true;
                verifyAuthCodeBtn.disabled = true;
                // 이메일 인증 성공 시 나머지 필드 활성화
                Object.keys(fields).forEach(key => {
                    fields[key].input.disabled = false;
                });
                profileImageInput.disabled = false;
                signupBtn.disabled = false; // 회원가입 버튼 활성화
            } else {
                isEmailVerified = false;
                const errorResult = await response.text(); // 오류 메시지를 텍스트로 받음
                showMessage(fields.emailAuthCode.error, errorResult || "인증번호가 일치하지 않거나 만료되었습니다.");
            }
        } catch (error) {
            console.error('인증번호 확인 오류:', error);
            showMessage(fields.emailAuthCode.error, "네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            isEmailVerified = false;
        }
    });

    // --- 회원가입 폼 제출 로직 ---
    signupForm.addEventListener('submit', async (event) => {
        event.preventDefault(); // 기본 폼 제출 동작 방지

        clearAllErrors(); // 기존 에러 메시지 초기화

        // 클라이언트 측 유효성 검사 (이메일 인증 여부 포함)
        if (!isEmailVerified) {
            showModal("이메일 인증을 먼저 완료해주세요.");
            return;
        }
        if (fields.password.input.value !== fields.confirmPassword.input.value) {
            showMessage(fields.confirmPassword.error, "비밀번호가 일치하지 않습니다.");
            showModal("입력 내용을 다시 확인해주세요.");
            return;
        }
        // 추가적인 클라이언트 측 유효성 검사 (예: 빈 필드, 형식 등)
        let allFieldsValid = true;
        for (const key in fields) {
            if (fields[key].input.required && !fields[key].input.value && fields[key].input.disabled === false) {
                showMessage(fields[key].error, "필수 입력 값입니다.");
                allFieldsValid = false;
            }
        }
        if (!allFieldsValid) {
            showModal("모든 필수 입력 필드를 채워주세요.");
            return;
        }

        // FormData 객체 생성
        const formData = new FormData();

        // memberSignupDTO 객체 생성 (emailAuthCode 포함)
        const memberSignupDTO = {
            username: fields.username.input.value,
            password: fields.password.input.value,
            confirmPassword: fields.confirmPassword.input.value,
            email: fields.email.input.value,
            emailAuthCode: fields.emailAuthCode.input.value, // 인증 코드 포함
            nickname: fields.nickname.input.value,
            birthDate: fields.birthDate.input.value,
            phoneNumber: fields.phoneNumber.input.value,
            name: document.getElementById('name').value // name 필드 추가
        };

        // memberSignupDTO를 JSON 문자열로 변환하여 FormData에 Blob으로 추가
        const memberSignupDTOBlob = new Blob([JSON.stringify(memberSignupDTO)], { type: 'application/json' });
        formData.append('memberSignupDTO', memberSignupDTOBlob);

        // 프로필 이미지 파일이 있을 경우 FormData에 추가
        if (profileImageInput.files.length > 0) {
            formData.append('profileImage', profileImageInput.files[0]);
        }

        try {
            const response = await fetch('/api/members/signup', {
                method: 'POST',
                body: formData
            });

            const result = await response.json(); // 응답을 JSON으로 파싱

            if (response.ok) { // HTTP 상태 코드가 200번대인 경우
                showModal("회원가입이 성공적으로 완료되었습니다.", () => {
                    window.location.href = '/main'; // 메인 페이지로 리다이렉트 (예시)
                });
            } else {
                let errorMessage = "회원가입 중 오류가 발생했습니다.";
                if (result.error) {
                    errorMessage = result.error;
                }
                showModal(errorMessage); // 실패 시 모달을 닫아도 페이지에 머무름

                // 백엔드에서 특정 필드에 대한 에러 메시지가 있다면 표시 (예상되는 응답 구조에 따라 조정)
                // 현재 백엔드는 Map<String, String> 형태로 "error" 키에 메시지를 담고 있으므로,
                // 필드별 에러 메시지 처리는 백엔드에서 별도 응답 구조를 제공해야 가능.
                // 예: { "error": "아이디가 이미 존재합니다.", "fieldErrors": { "username": "이미 사용 중인 아이디입니다." } }
                // 현재는 result.error만 활용.
            }
        } catch (error) {
            console.error('회원가입 Fetch 오류:', error);
            showModal("네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    });

    // --- 모달 기능 함수 ---
    function showModal(message, confirmCallback) {
        modalMessage.textContent = message;
        modal.style.display = 'flex'; // Flexbox로 중앙 정렬

        modalConfirmBtn.onclick = () => {
            modal.style.display = 'none';
            if (confirmCallback) {
                confirmCallback();
            }
        };

        // 모달 외부 클릭 시 닫기 (실패 시에는 페이지를 벗어나지 않도록)
        window.onclick = (event) => {
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        };
    }
});
