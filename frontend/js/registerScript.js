let user = document.getElementById('username')
let pwd = document.getElementById('password')
let confirmPwd = document.getElementById('confirmPwd')
let btn = document.getElementById('registerBtn')
let tip = document.getElementById('errorTip')
let success = document.getElementById('successTip')

btn.onclick = function(){
    tip.innerText = '';
    success.innerText = '';

    let name = user.value.trim();
    let pass = pwd.value.trim();
    let confirm = confirmPwd.value.trim();

    if(name === ''){
        tip.innerText = '请输入学号';
        return;
    }

    if(name.length < 3){
        tip.innerText = '学号至少3个字符';
        return;
    }

    if(pass === ''){
        tip.innerText = '请输入密码';
        return;
    }

    if(pass.length < 6){
        tip.innerText = '密码至少6个字符';
        return;
    }

    if(confirm !== pass){
        tip.innerText = '两次输入的密码不一致';
        return;
    }

    // 发送注册请求到后端 Java 服务器
    btn.disabled = true;
    btn.innerText = '注册中...';

    fetch('http://localhost:8080/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: name, password: pass })
    })
    .then(function(response) {
        return response.json();
    })
    .then(function(data) {
        btn.disabled = false;
        btn.innerText = '注册';
        if (data.success) {
            success.innerText = '注册成功！即将跳转到登录页...';
            setTimeout(function(){
                location.href = 'login.html';
            }, 1500);
        } else {
            tip.innerText = data.message || '注册失败';
        }
    })
    .catch(function(error) {
        btn.disabled = false;
        btn.innerText = '注册';
        tip.innerText = '无法连接服务器，请稍后重试';
        console.error('Register error:', error);
    });
}
