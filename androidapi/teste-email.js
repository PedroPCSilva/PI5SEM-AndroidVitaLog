const nodemailer = require('nodemailer');

async function testarEnvio() {
    // CONFIGURAÇÃO
    const transporter = nodemailer.createTransport({
        service: 'gmail',
        auth: {
            user: 'pis.sen4c.bs1@gmail.com', 
            pass: 'rcptxxpdvlnwzzkc' 
        }
    });

    try {
        console.log("Tentando autenticar...");
        // Tenta verificar se a senha está certa antes de enviar
        await transporter.verify();
        console.log("✅ Autenticação com Google: SUCESSO!");

        console.log("Tentando enviar e-mail...");
        await transporter.sendMail({
            from: 'Teste <pis.senac.bs1@gmail.com>',
            to: 'gerenciapedropaulocav@gmail.com', // Envia para você mesmo testar
            subject: 'Teste de Configuração',
            text: 'Se você leu isso, o envio está funcionando!'
        });
        console.log("✅ E-mail enviado com sucesso!");

    } catch (error) {
        console.log("❌ ERRO FATAL:");
        console.log(error.message); // Isso vai nos dizer o motivo exato
        
        if (error.code === 'EAUTH') {
            console.log("\nDICA: O erro é de SENHA ou PERMISSÃO.");
            console.log("1. Você ativou a Verificação em Duas Etapas no Google?");
            console.log("2. Você gerou a 'Senha de App' e colou ela aqui?");
            console.log("3. Você NÃO pode usar sua senha normal de login.");
        }
    }
}

testarEnvio();