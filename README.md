
# Event Manager

Este repositório contém o trabalho da matéria optativa de "Desenvolvimento Back-End", do curso de Ciência da Computação do IFMG - Campus Formiga.

O intúito era desenvolver um sistema de gerenciamento de eventos composto por backend (Spring) e frontend (Angular), utilizando PostgreSQL como banco de dados.

<div align="center"> <table> <tr> <td><img src="https://github.com/user-attachments/assets/35f6337c-446e-49c1-b53b-3d4ee14c3a22" width="450" /></td> <td><img src="https://github.com/user-attachments/assets/45056bcd-b67e-4e43-960c-a1656c1f60a9" width="450"/>
</td> </tr> <tr> <td><img src="https://github.com/user-attachments/assets/2b526932-22e0-4122-86b6-f9c08e3c4980" width="450" /></td> <td><img src="https://github.com/user-attachments/assets/a4d81983-bbfc-40ed-8057-f8b5571df220" width="450"/>
</td> </tr> </table> </div>


## Pré-requisitos
- [Docker](https://www.docker.com/get-started) e [Docker Compose](https://docs.docker.com/compose/install/)
- [JDK 17+](https://adoptium.net/) (para o backend Spring)
- [Node.js](https://nodejs.org/) e [npm](https://www.npmjs.com/) (para o frontend Angular)

## Estrutura do Projeto

```
event-manager-w-front/
├── api/               # Backend em Spring
├── front/             # Frontend em Angular
│   └── EventManager/  # Aplicação Angular
├── docker-compose.yml # Configuração do banco de dados

```
## Configuração e Inicialização
### 1. Banco de Dados (PostgreSQL)
O projeto utiliza um banco de dados PostgreSQL configurado via Docker Compose:

``` bash
# Na raiz do projeto
docker-compose up -d
```
Isso iniciará um contêiner PostgreSQL com as seguintes configurações:
- **Porta**: 5432
- **Usuário**: event-manager
- **Senha**: e193U2JY
- **Banco de dados**: event-manager_db

### 2. Backend (Spring)

#### 2.1 Variáveis de Ambiente
O projeto backend utiliza as seguintes variáveis de ambiente que podem ser configuradas:
- `EMAIL_HOST`: Host do servidor SMTP (padrão: smtp.gmail.com)
- `EMAIL_PORT`: Porta do servidor SMTP (padrão: 587)
- `EMAIL_USERNAME`: Usuário para autenticação no servidor de email
- `EMAIL_PASSWORD`: Senha para autenticação no servidor de email
- : ID do cliente para segurança (padrão: myclientid) `CLIENT_ID`
- : Chave secreta do cliente (padrão: myclientsecret) `CLIENT_SECRET`
- : Duração do token JWT em segundos (padrão: 86400) `JWT_DURATION`
- : Lista de origens permitidas para CORS (padrão: [http://localhost:3000](http://localhost:3000),[http://localhost:5173](http://localhost:5173),[http://localhost:4200](http://localhost:4200)) `CORS_ORIGINS`

Estas variáveis podem ser configuradas no ambiente ou nos arquivos ou . Existem arquivos de exemplo (*.example) que podem ser usados como base. `application.properties``application-dev.properties`


Para iniciar o backend:
``` bash
# Navegue até a pasta api
cd api

# Em sistemas Unix/Linux/macOS
./mvnw spring-boot:run

# Em Windows
mvnw.cmd spring-boot:run
```
O servidor backend ficará disponível em `http://localhost:8080`.

### 3. Frontend (Angular)

Para iniciar o frontend:
``` bash
# Navegue até a pasta da aplicação Angular
cd front/EventManager

# Instale as dependências
npm install

# Inicie o aplicativo angular
ng serve
```
O aplicativo Angular ficará disponível em `http://localhost:4200`.

## Desenvolvimento
- **Backend**: O código do backend está na pasta `api/src`
- **Frontend**: O código do frontend está na pasta `front/EventManager`

## Notas Adicionais
- O banco de dados é persistente graças ao volume Docker configurado (`pgdata`)
- Certifique-se de que as portas 5432 (PostgreSQL), 8080 (Spring) e 4200 (Angular) estejam livres em sua máquina
- Para utilizar o serviço de email, configure as variáveis de ambiente `EMAIL_USERNAME` e `EMAIL_PASSWORD` com credenciais válidas

## Autores

- [@H1gor](https://github.com/H1gor1)
- [@AnnaKareninaLopes](https://github.com/AnnaKareninaLopes)
