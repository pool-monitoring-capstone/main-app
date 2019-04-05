from twilio.rest import Client

account_sid = ''
auth_token = ''
client = Client(account_sid, auth_token)

def send_sms(phoneNumner, body):
    message = client.messages \
                .create(
                     body=body,
                     from_='+18679881612',
                     to=phoneNumner
                )

send_sms('+1','Alert - Detected a possible case of drowning in the pool. Please - Review Footage. Should we call 911? ')
