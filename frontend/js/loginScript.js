let user = document.getElementById('username')
let pwd = document.getElementById('password')
let btn = document.getElementById('loginBtn')
let tip = document.getElementById('errorTip')

btn.onclick = function(){
    tip.innerText = '';

    let name = user.value.trim();
    let pass = pwd.value.trim();

    if(name === ''){
        tip.innerText = '请输入账号'
        return
    }

    if(pass === '')
    {
        tip.innerText = '请输入密码'
        return
    }

    // 发送登录请求到后端验证
    btn.disabled = true;
    btn.innerText = '登录中...';

    fetch('http://localhost:8080/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: name, password: pass })
    })
    .then(function(response) {
        return response.json();
    })
    .then(function(data) {
        btn.disabled = false;
        btn.innerText = '登录';
        if (data.success) {
            // 登录成功，可保存用户信息到 sessionStorage
            sessionStorage.setItem('userId', name);
            location.href = 'home.html';
        } else {
            tip.innerText = data.message || '账号或密码错误';
        }
    })
    .catch(function(error) {
        btn.disabled = false;
        btn.innerText = '登录';
        tip.innerText = '无法连接服务器，请稍后重试';
        console.error('Login error:', error);
    });
}