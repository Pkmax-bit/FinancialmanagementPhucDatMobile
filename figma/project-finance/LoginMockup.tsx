import React from 'react';
import { Mail, Lock, Eye } from 'lucide-react';

export default function LoginMockup() {
  return (
    <div className="w-[390px] h-[844px] bg-gradient-to-br from-[#2196F3] to-[#1976D2] rounded-3xl shadow-2xl overflow-hidden flex flex-col">
      {/* Status Bar */}
      <div className="h-11 bg-black/20 flex items-center justify-between px-6 text-white text-xs">
        <span>9:41</span>
        <div className="flex gap-1 items-center">
          <div className="w-4 h-3 border border-white rounded-sm" />
          <div className="w-1 h-3 bg-white rounded-sm" />
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 flex flex-col items-center justify-center px-8">
        {/* Logo */}
        <div className="w-24 h-24 bg-white rounded-3xl mb-8 flex items-center justify-center shadow-xl">
          <svg className="w-16 h-16 text-[#2196F3]" fill="currentColor" viewBox="0 0 24 24">
            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
          </svg>
        </div>

        <h1 className="text-white mb-2">Project Finance Manager</h1>
        <p className="text-white/80 mb-12">Quản lý tài chính dự án</p>

        {/* Login Form */}
        <div className="w-full space-y-4">
          {/* Email Input */}
          <div className="bg-white/95 rounded-xl px-4 py-4 flex items-center gap-3 shadow-lg">
            <Mail className="w-5 h-5 text-[#2196F3]" />
            <input
              type="email"
              placeholder="Email"
              className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
              disabled
            />
          </div>

          {/* Password Input */}
          <div className="bg-white/95 rounded-xl px-4 py-4 flex items-center gap-3 shadow-lg">
            <Lock className="w-5 h-5 text-[#2196F3]" />
            <input
              type="password"
              placeholder="Mật khẩu"
              className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
              disabled
            />
            <Eye className="w-5 h-5 text-slate-400" />
          </div>

          {/* Login Button */}
          <button className="w-full bg-white text-[#2196F3] py-4 rounded-xl shadow-lg transition-transform active:scale-95">
            Đăng nhập
          </button>

          {/* Links */}
          <div className="flex justify-between items-center pt-4">
            <button className="text-white/90 text-sm">Quên mật khẩu?</button>
            <button className="text-white/90 text-sm">Đăng ký tài khoản</button>
          </div>
        </div>
      </div>

      {/* Footer */}
      <div className="pb-8 text-center text-white/60 text-xs">
        © 2025 Project Finance Manager
      </div>
    </div>
  );
}
