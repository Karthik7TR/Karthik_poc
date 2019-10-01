$(document).ready(function(){
    $('.delete-employee').on('click',function(){
        var id = $(this).data('id');
        var url = '/delete/'+id;
        if(confirm('Delete Employee?')){
            $.ajax({
                url: url,
                type: 'DELETE',
                success: function(result){
                    console.log('Deleting Employee...');
                    window.location.href='/';
                },
                error: function(err){
                    console.log(err);
                }
            });
        }
    });
    


});


