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

    if(name === 'admin' && pass === '123456'){
        location.href='home.html'
    }else{
        tip.innerText = '账号或密码错误';
    }
}