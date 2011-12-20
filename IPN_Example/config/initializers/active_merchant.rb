if Rails.env.production?
  PAYPAL_ACCOUNT = 'your_account@yourbusiness.com'
else
  
  PAYPAL_ACCOUNT = 'svigra_1322573821_biz@gmail.com'
  ActiveMerchant::Billing::Base.mode = :test
end