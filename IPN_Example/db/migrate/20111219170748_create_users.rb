class CreateUsers < ActiveRecord::Migration
  def change
    create_table :users do |t|
      t.string :first_name
      t.string :last_name
      t.string :email
      t.string :phone
      t.string :city
      t.string :state
      t.string :country
      t.string :zip
      t.string :address1
      t.timestamps
    end
  end
end
