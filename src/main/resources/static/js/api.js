const API_BASE=(typeof window!=='undefined'&&window.location&&window.location.origin)?window.location.origin:'http://localhost:8080';
function buildUrl(url){return /^https?:\/\//.test(url)?url:API_BASE+url}
async function apiRequest(method,url,body){
  const headers={'Content-Type':'application/json','Accept':'application/json'};
  const token=(localStorage.getItem('token')||'').trim();
  const isAuthPath=/^\s*\/auth\//.test(url);
  if(token&&!isAuthPath){headers['Authorization']='Bearer '+token}
  try{console.log('apiRequest',method,url,'auth',!isAuthPath&&!!token,'tokPref',token?token.slice(0,12):'<none>','len',token?token.length:0)}catch(e){}
  const res=await fetch(buildUrl(url),{method,headers,body:body?JSON.stringify(body):undefined});
  let data=null;try{data=await res.json()}catch(e){}
  if(res.status===401){
    if(/^\s*\/auth\//.test(url)){
      try{localStorage.removeItem('token');localStorage.removeItem('username')}catch(e){}
      try{if(typeof window!=='undefined'){window.location.href='index.html'}}catch(e){}
      throw new Error('Credenciales inválidas');
    }
    const msg=(data&&data.error)||'Operación no autorizada';
    throw new Error(msg)
  }
  if(!res.ok){const err=(data&&data.message)||'Error en la solicitud';throw new Error(err)}
  return data
}