// 1. https 모듈 로딩
const https = require('https')
fs = require('fs')

// 2. 생성한 openssl key 파일 로딩
// Linux/Mac 환경에서는 fs.readFileSync('serverpem/private.pem')
var options = {
    key : fs.readFileSync('serverpem/private.pem','utf-8'),
    cert : fs.readFileSync('serverpem/public.pem','utf-8')
}

// 3. https 443 포트로 서버 구동
var https_server = https.createServer( options,(req,res) => {
    fs.readFile('artest.html',function(error,data){
        if(error){
            console.log(error);
        }
        else{
            res.writeHead(200);
            res.end(data);
        }
    })
}).listen(443);