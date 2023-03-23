# Java-Generic-Rest-Api

API Rest developed using *Java*. Its purpose is to serve as a POC of https://github.com/LeonardoRamos/generic-rest-core-lib to make it easier to provide and serve data with JPA from any API that uses it as a framework.


## Setting up

To test the project locally, simply clone the repository and import into *Eclipse* (or any other IDE of your choice) as a *Maven* project.
After that, you will need to run `maven` install and then run the project using the IDE.


## API

The API accepts filters, sorting, aggregation functions, grouping and field projection.
For authentications, it uses JWT.


### Filter
The available options of filters to be applied:

- Equals: "=eq=" or "=" (may be used to compare if value is equal to `null`)

- Less than or equal: "=le=" or "<="

- Greater than or equal: "=ge=" or ">="

- Greater than: "=gt=" or ">"

- Less than: "=lt=" or "<"

- Not equal: "=ne=" or "!=" (may be used to compare if the value is other than `null`)

- In: "=in="

- Out: "=out="

- Like: "=like="

Logical operators in the url:

- AND: "\_and\_" or just ";"
- OR: "\_or\_" or just ","


`filter = [field1=value,field2=like=value2;field3!=value3...]`

### Projection
The Projection follows the following syntax, and the json response will only have with the specified fields:

`projection = [field1, field2, field3...]`

### Sort
The Sorting follows the following syntax (where `sortOrder` may be `asc` or `desc`):

`sort = [field1 = sortOrder, field2 = sortOrder...]`

### GroupBy
GroupBy follows the following syntax (*groupBy* does not accept *projections* parameters and is expected to be used along with an aggregation function):

`groupBy = [field1, field2, field3...]`

### Sum
It performs Sum function in the specified fields, and follows the following syntax:

`sum = [field1, field2, field3...]`

### Avg
It performs function of Avg in the specified fields, and follows the following syntax:

`avg = [field1, field2, field3...]`

### Count
It performs Count function in the specified fields, and follows the following syntax:

`count = [field1, field2, field3...]`

### Count Distinct
It performs Count Distinct function in the specified fields, and follows the following syntax:

`countDistinct = [field1, field2, field3...]`

### Error response format

```json
{
   "errors":[
      {
         "code":"ERROR_CODE",
         "message":"Error parsing projections of filter [unknownField]"
      }
   ]
}
```

### Extra Parameters
- offset (DEFAULT_OFFSET = 0)
- limit (DEFAULT_LIMIT = 20 and MAX_LIMIT = 100)

