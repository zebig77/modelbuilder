package sandbox




def nodeBuilder = new NodeBuilder()
def userlist = nodeBuilder.userlist {
    user(id: '1', firstname: 'John', lastname: 'Smith') {
        address(type: 'home', street: '1 Main St.', city: 'Springfield', state: 'MA', zip: '12345')
        address(type: 'work', street: '2 South St.', city: 'Boston', state: 'MA', zip: '98765')
    }
    user(id: '2', firstname: 'Alice', lastname: 'Doe')
}

println userlist