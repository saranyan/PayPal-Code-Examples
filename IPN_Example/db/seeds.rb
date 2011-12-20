# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)

user = User.create(first_name: 'john', last_name: 'doe', phone: '512-001-0020', email: 'jd@nameless.com', city: 'austin', state: 'TX', zip: '78759', address1: '9600 N Mopac expy', country: 'USA')
 
