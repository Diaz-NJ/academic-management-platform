<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Academic Management Platform</title>
    <link rel="stylesheet" href="css/styles.css">
    <style>
        .login-container {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background: linear-gradient(135deg, #3788d8, #5ba3e8);
        }
        
        .login-box {
            background: white;
            padding: 3rem;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            width: 400px;
        }
        
        .login-box h2 {
            text-align: center;
            margin-bottom: 2rem;
            color: #3788d8;
        }
        
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
        }
        
        .form-group input {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            font-size: 1rem;
        }
        
        .login-btn {
            width: 100%;
            padding: 1rem;
            background-color: #3788d8;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        
        .login-btn:hover {
            background-color: #2c6db5;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-box">
            <h2>AMP Login</h2>
            <p style="text-align: center; color: #6c757d; margin-bottom: 2rem;">
                Academic Management Platform
            </p>
            <form id="loginForm">
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" required>
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" required>
                </div>
                <button type="submit" class="login-btn">Login</button>
            </form>
            <p style="text-align: center; margin-top: 1.5rem; color: #6c757d;">
                Don't have an account? <a href="register.html" style="color: #3788d8;">Register</a>
            </p>
        </div>
    </div>
    
    <script>
        document.getElementById('loginForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            
            // TODO: Implement actual authentication
            // For now, redirect to main page
            window.location.href = 'index.html';
        });
    </script>
</body>
</html>