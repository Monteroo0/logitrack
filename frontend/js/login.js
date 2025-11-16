const form=document.getElementById('loginForm');
const modal=document.getElementById('errorModal');
const closeBtn=document.getElementById('closeModal');
const errorText=document.getElementById('errorText');
function showError(t){errorText.textContent=t;modal.classList.remove('hidden')}
function hideError(){modal.classList.add('hidden')}
closeBtn.addEventListener('click',hideError)
form.addEventListener('submit',async function(e){e.preventDefault();hideError();const username=document.getElementById('username').value.trim();const password=document.getElementById('password').value;try{const res=await apiRequest('POST','/auth/login',{username,password});const token=res.token||res.accessToken||res.jwt;if(!token){showError('Token no recibido');return}localStorage.setItem('token',token);localStorage.setItem('username',res.username||username);window.location.href='dashboard.html'}catch(err){showError(err.message||'Credenciales inválidas')}})