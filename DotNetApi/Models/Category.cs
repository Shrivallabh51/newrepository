﻿using System;
using System.Collections.Generic;

namespace DotNetApi.Models
{
    public partial class Category
    {
        public Category()
        {
            Products = new HashSet<Product>();
        }

        public int CatId { get; set; }
        public string CatName { get; set; } = null!;
        public string Status { get; set; } = null!;

        public virtual ICollection<Product> Products { get; set; }
    }
}
