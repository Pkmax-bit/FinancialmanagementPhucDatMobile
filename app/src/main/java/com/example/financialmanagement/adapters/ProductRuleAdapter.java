package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.MaterialAdjustmentRule;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.util.List;

public class ProductRuleAdapter extends RecyclerView.Adapter<ProductRuleAdapter.RuleViewHolder> {

    private List<MaterialAdjustmentRule> rules;
    private RuleClickListener clickListener;

    public interface RuleClickListener {
        void onRuleClick(MaterialAdjustmentRule rule);
    }

    public ProductRuleAdapter(List<MaterialAdjustmentRule> rules, RuleClickListener clickListener) {
        this.rules = rules;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_rule, parent, false);
        return new RuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuleViewHolder holder, int position) {
        MaterialAdjustmentRule rule = rules.get(position);
        holder.bind(rule, clickListener);
    }

    @Override
    public int getItemCount() {
        return rules != null ? rules.size() : 0;
    }

    public void updateRules(List<MaterialAdjustmentRule> newRules) {
        this.rules = newRules;
        notifyDataSetChanged();
    }

    static class RuleViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvType, tvValue;

        public RuleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_rule_name);
            tvType = itemView.findViewById(R.id.tv_rule_type);
            tvValue = itemView.findViewById(R.id.tv_rule_value);
        }

        public void bind(MaterialAdjustmentRule rule, RuleClickListener listener) {
            tvName.setText(rule.getName());
            
            String type = rule.getAdjustmentType();
            if (type != null) {
                 tvType.setText(type.toUpperCase());
            } else {
                tvType.setText("UNKNOWN");
            }
            
            Double val = rule.getAdjustmentValue();
            if (val != null) {
                if ("percentage".equalsIgnoreCase(type)) {
                    tvValue.setText("Giá trị: " + val + "%");
                } else {
                    tvValue.setText("Giá trị: " + CurrencyFormatter.format(val));
                }
            } else {
                 tvValue.setText("Giá trị: N/A");
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onRuleClick(rule);
            });
        }
    }
}
