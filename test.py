import smtplib

def test_smtp_connection():
    try:
        with smtplib.SMTP('smtp.outlook.com', 587) as server:
            server.starttls()
            server.login('aubsocialapp@outlook.com', 'SocialApp1234')
            server.quit()
        return True
    except Exception as e:
        print(f"Error: {e}")
        return False

if __name__ == "__main__":
    if test_smtp_connection():
        print("SMTP connection successful.")
    else:
        print("SMTP connection failed.")
