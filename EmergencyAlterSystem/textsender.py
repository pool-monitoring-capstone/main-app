from twilio.rest import Client

account_sid = 'AC3271de3b0b95b18d00ca1febab4db43f'
auth_token = '5c899e83042f96227a7b112d163b88a7'
client = Client(account_sid, auth_token)

def send_sms(phoneNumner, body):
    message = client.messages \
                .create(
                     body=body,
                     from_='+18679881612',
                     to=phoneNumner
                )

send_sms('+1','Alert - Detected a possible case of drowning in the pool. Please - Review Footage. Should we call 911? ')
